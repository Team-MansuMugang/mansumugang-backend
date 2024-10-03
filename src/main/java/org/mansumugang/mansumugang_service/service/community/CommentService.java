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
import org.mansumugang.mansumugang_service.service.user.UserCommonService;
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
    private final UserCommonService userCommonService;

    private final int COMMENT_PAGE_SIZE = 10;
    private final int REPLY_PAGE_SIZE = 5;
    @Value("${file.upload.image.api}")
    private String imageApiUrl;


    @Transactional
    public CommentSave.Dto saveComment(User user, CommentSave.Request request){

        Protector validProtector = userCommonService.findProtector(user);

        Post foundPost = postRepository.findById(request.getPostId()).orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchPostError));

        Comment savedComment = commentRepository.save(Comment.of(request, foundPost, validProtector));

        sendMessageToPostCreator(validProtector, foundPost);

        return CommentSave.Dto.fromEntity(savedComment);

    }


    public CommentInquiry.Response getCommentList(User user, Long cursor, Long postId){

        userCommonService.findProtector(user);

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

        Protector validProtector = userCommonService.findProtector(user);

        Comment foundComment = commentRepository.findById(request.getCommentId())
                .orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchCommentError));

        if (foundComment.getDeletedAt() != null){
            throw new CustomErrorException(ErrorType.DeletedCommentError);
        }

        if (!foundComment.getProtector().getUsername().equals(validProtector.getUsername())){
            throw new CustomErrorException(ErrorType.NotTheAuthorOfTheComment);
        }

        foundComment.update(request.getContent());

        return CommentUpdate.Dto.fromEntity(foundComment);
    }

    @Transactional
    public CommentDelete.Dto deleteComment(User user, Long commentId){

        Protector validProtector = userCommonService.findProtector(user);

        Comment foundComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchCommentError));

        if (foundComment.getDeletedAt() != null){
            throw new CustomErrorException(ErrorType.DeletedCommentError);
        }

        if (!foundComment.getProtector().getUsername().equals(validProtector.getUsername())){
            throw new CustomErrorException(ErrorType.NotTheAuthorOfTheComment);
        }

        foundComment.delete();

        return CommentDelete.Dto.fromEntity(foundComment);

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
