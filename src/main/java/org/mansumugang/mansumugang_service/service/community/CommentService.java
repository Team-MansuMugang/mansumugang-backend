package org.mansumugang.mansumugang_service.service.community;


import com.google.firebase.messaging.Message;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mansumugang.mansumugang_service.constant.ErrorType;
import org.mansumugang.mansumugang_service.domain.community.Comment;
import org.mansumugang.mansumugang_service.domain.community.Post;
import org.mansumugang.mansumugang_service.domain.fcm.FcmToken;
import org.mansumugang.mansumugang_service.domain.user.Protector;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.dto.community.comment.CommentDelete;
import org.mansumugang.mansumugang_service.dto.community.comment.CommentInquiry;
import org.mansumugang.mansumugang_service.dto.community.comment.CommentSave;
import org.mansumugang.mansumugang_service.dto.community.comment.CommentUpdate;
import org.mansumugang.mansumugang_service.exception.CustomErrorException;
import org.mansumugang.mansumugang_service.repository.CommentRepository;
import org.mansumugang.mansumugang_service.repository.FcmTokenRepository;
import org.mansumugang.mansumugang_service.repository.PostRepository;
import org.mansumugang.mansumugang_service.repository.ReplyRepository;
import org.mansumugang.mansumugang_service.service.fcm.FcmService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final FcmTokenRepository fcmTokenRepository;

    private final FcmService fcmService;

    private final int COMMENT_PAGE_SIZE = 10; // 한페이지 당 최대 댓글수 : 10개
    private final int REPLY_PAGE_SIZE = 5; //  한페이지 당 최대 대댓글수 : 5개

    @Value("${file.upload.image.api}")
    private String imageApiUrl;


    @Transactional
    public CommentSave.Dto saveComment(User user, CommentSave.Request request){

        // 1. user가 보호자 객체인지 검증
        Protector validProtector = validateProtector(user);

        // 2. 넘겨받은 postId로 게시물 조회 -> 없으면 예외처리.
        Post foundPost = postRepository.findById(request.getPostId()).orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchPostError));

        // 3. 찾은 게시물에 댓글 저장.
        Comment savedComment = commentRepository.save(Comment.of(request, foundPost, validProtector));

        // 4. 댓글 등록시 게시물 작성자에게 메시지 전송.(게시물 작성자 본인이 본인의 게시물의 댓글을 등록할 경우 제외.)
        sendMessageToPostCreator(validProtector, foundPost);

        return CommentSave.Dto.fromEntity(savedComment);

    }


    public CommentInquiry.Response getCommentList(User user, Long cursor, Long postId){

        validateProtector(user);

        Page<Comment> commentPage;

        Post foundPost = postRepository.findById(postId).orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchPostError));

        Pageable commentPageable = PageRequest.of(0, COMMENT_PAGE_SIZE);

        if(cursor != null){
            Comment foundComment = commentRepository.findById(cursor)
                    .orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchCommentError));

            commentPage = commentRepository.getCommentsByCursor(foundPost, foundComment.getId(), foundComment.getCreatedAt(), commentPageable);
        }else {
            commentPage =commentRepository.findAllByPost(foundPost, commentPageable);
        }

        Pageable replyPageable = PageRequest.of(0, REPLY_PAGE_SIZE, Sort.by(Sort.Direction.ASC, "createdAt"));
        List<CommentInquiry.CommentDto> commentList = commentPage.map(comment1 -> CommentInquiry.CommentDto.of(comment1, replyRepository.findAllByComment(comment1, replyPageable),imageApiUrl)).toList();

        return CommentInquiry.Response.of(commentList, imageApiUrl);
    }

    @Transactional
    public CommentUpdate.Dto updateComment(User user, CommentUpdate.Request request){
        // 1. 넘겨받은 user 가 보호자 객체인지 검증
        Protector validProtector = validateProtector(user);

        // 2. request의 commentId로 댓글 찾기 -> 없으면 예외 처리
        Comment foundComment = commentRepository.findById(request.getCommentId())
                .orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchCommentError));

        // 3. comment 의 deletedAt이 null이 아니면 삭제된 댓글 -> 예외 처리
        if (foundComment.getDeletedAt() != null){
            throw new CustomErrorException(ErrorType.DeletedCommentError);
        }

        // 4. 댓글을 수정하려는 유저와 해당 댓글의 작성자를 검증.
        if (!foundComment.getProtector().getUsername().equals(validProtector.getUsername())){
            throw new CustomErrorException(ErrorType.NotTheAuthorOfTheComment);
        }

        // 5. 댓글 수정 시작.
        foundComment.update(request.getContent());

        return CommentUpdate.Dto.fromEntity(foundComment);
    }

    @Transactional
    public CommentDelete.Dto deleteComment(User user, Long commentId){

        // 1. 넘겨받은 user가 보호자 인스턴스인지 검증
        Protector validProtector = validateProtector(user);

        // 2. 경로변수로 받은 댓글아이디로 댓글 찾기 -> 없으면 예외처리.
        Comment foundComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchCommentError));

        // 3. foundComment의 deletedAt이 null 이 아니라면 이미 삭제된 댓글임(예외처리)
        if (foundComment.getDeletedAt() != null){
            throw new CustomErrorException(ErrorType.DeletedCommentError);
        }

        // 4. 댓글의 삭제하려는 유저와 댓글의 작성자가 같은지 검증 -> 틀리면 예외 처리
        if (!foundComment.getProtector().getUsername().equals(validProtector.getUsername())){
            throw new CustomErrorException(ErrorType.NotTheAuthorOfTheComment);
        }

        // 5. 댓글 삭제
        foundComment.delete();

        return CommentDelete.Dto.fromEntity(foundComment);

    }


    private Protector validateProtector(User user){
        log.info("AuthenticationPrincipal 로 받은 유저 객체가 보호자 객체인지 검증 시작");
        if (user == null) {
            throw new CustomErrorException(ErrorType.UserNotFoundError);
        }

        if (user instanceof Protector) {

            log.info("보호자 객체 검증 완료");
            return (Protector) user;
        }

        throw new CustomErrorException(ErrorType.AccessDeniedError);
    }

    private void sendMessageToPostCreator(Protector validProtector, Post foundPost) {
        if (!validProtector.getUsername().equals(foundPost.getProtector().getUsername())) {
            List<FcmToken> foundFcmTokens = fcmTokenRepository.findByProtectorId(foundPost.getProtector().getId());
            for (FcmToken fcmToken : foundFcmTokens) {
                Message message = Message.builder()
                        .putData("title", "만수무강")
                        .putData("body", "회원님께서 작성하신 게시물에 댓글이 달렸어요!")
                        .setToken(fcmToken.getFcmToken())
                        .build();

                fcmService.sendMessage(message);
            }
        }
    }
}
