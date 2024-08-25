package org.mansumugang.mansumugang_service.service.community;


import com.google.firebase.messaging.Message;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mansumugang.mansumugang_service.constant.ErrorType;
import org.mansumugang.mansumugang_service.domain.community.Comment;
import org.mansumugang.mansumugang_service.domain.community.Post;
import org.mansumugang.mansumugang_service.domain.community.Reply;
import org.mansumugang.mansumugang_service.domain.fcm.FcmToken;
import org.mansumugang.mansumugang_service.domain.user.Protector;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.dto.community.reply.ReplyDelete;
import org.mansumugang.mansumugang_service.dto.community.reply.ReplyInquiry;
import org.mansumugang.mansumugang_service.dto.community.reply.ReplySave;
import org.mansumugang.mansumugang_service.dto.community.reply.ReplyUpdate;
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

@Service
@Slf4j
@RequiredArgsConstructor
public class ReplyService {

    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final FcmTokenRepository fcmTokenRepository;

    private final FcmService fcmService;

    private final int REPLY_PAGE_SIZE = 5; // 한페이지당 대댓글 수 : 5

    @Value("${file.upload.image.api}")
    private String imageApiUrl;

    @Transactional
    public ReplySave.Dto saveReply(User user, ReplySave.Request request){

        // 1. user가 보호자 객체인지 검증
        Protector validProtector = validateProtector(user);

        // 2. 넘겨받은 postId로 게시물 조회 -> 없으면 예외처리.
        Comment foundComment = commentRepository.findById(request.getCommentId()).orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchCommentError));

        // 3. 찾은 게시물에 댓글 저장.
        Reply savedReply = replyRepository.save(Reply.of(request, foundComment, validProtector));

        // TODO : 댓글 및 게시물 작성자에게 알림 전송
        sendMessageToPostAndCommentCreator(foundComment, validProtector);

        return ReplySave.Dto.fromEntity(savedReply);

    }

    public ReplyInquiry.Response getReplyList(User user, Long cursor, Long commentId){

        validateProtector(user);

        // 1. 넘겨받은 댓글고유번호로 댓글 찾기.
        Comment foundComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchCommentError));

        // 2. cursor가 값이 존재하면 그 값 이후의 id를 갖는 대댓글들을 출력함. -> EX) cursor == 3 -> id가 4인 대댓글 부터 보여줌.
        if (cursor != null){
            Reply foundReply = replyRepository.findById(cursor)
                    .orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchReplyError));

            Pageable replyPageable = PageRequest.of(0, REPLY_PAGE_SIZE);
            Page<Reply> replyPage = replyRepository.getRepliesByCursor(foundComment, foundReply.getId(), foundReply.getCreatedAt(), replyPageable);

            return ReplyInquiry.Response.fromPage(replyPage, imageApiUrl);
        }else{
            Pageable replyPageable = PageRequest.of(0, REPLY_PAGE_SIZE, Sort.by(Sort.Direction.ASC, "createdAt"));
            Page<Reply> replyPage = replyRepository.findAllByComment(foundComment, replyPageable);

            return ReplyInquiry.Response.fromPage(replyPage, imageApiUrl);
        }

    }

    @Transactional
    public ReplyUpdate.Dto updateReply(User user, ReplyUpdate.Request request){

        // 1. 넘겨받은 유저가 보호자 객체인지 검증
        Protector validProtector = validateProtector(user);

        // 2. 넘겨받은 request의 대댓글 아이디로 레포지토리에서 해당 대댓글 조회 -> 없으면 예외처리.
        Reply foundReply = replyRepository.findById(request.getReplyId())
                .orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchReplyError));

        // 3. 찾은 대댓글의 deletedAt 이 null 이 아니라면 예외처리(null 이면 삭제된 대댓글임.)
        if (foundReply.getDeletedAt() != null){
            throw new CustomErrorException(ErrorType.DeletedReplyError);
        }

        // 4. 대댓글의 아이디와 현제 엑세스된 유저의 아이디가 같지 않으면 예외 처리.
        if (!foundReply.getProtector().getUsername().equals(validProtector.getUsername())){
            throw new CustomErrorException(ErrorType.NotTheAuthorOfTheReply);
        }

        // 5. 모든 검증을 마쳤다면 댓글 업데이트.
        foundReply.update(request.getContent());

        return ReplyUpdate.Dto.of(foundReply);
    }

    @Transactional
    public ReplyDelete.Dto deleteReply(User user, Long replyId){

        // 1. 넘겨받은 user가 보호자 객체인지 검증
        Protector validProtector = validateProtector(user);

        // 2. 경로변수로 받은 아이디로 대댓글 조회 없으면 예외 처리.
        Reply foundReply = replyRepository.findById(replyId)
                .orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchReplyError));

        // 3. deleteAt이 null이 아니라면 이미 삭제된 대댓글임.
        if (foundReply.getDeletedAt() != null){
            throw new CustomErrorException(ErrorType.DeletedReplyError);
        }

        // 4. 삭제할 대댓글의 작성자(아이디)와 넘겨받은 user(아이디) 가 일치 하지 않으면 예외 처리.
        if (!foundReply.getProtector().getUsername().equals(validProtector.getUsername())){
            throw new CustomErrorException(ErrorType.NotTheAuthorOfTheReply);
        }

        // 5. 검증이 완료되었다면 대댓글 삭제 진행.
        foundReply.delete();

        return ReplyDelete.Dto.fromEntity(foundReply);
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

    private void sendMessageToPostAndCommentCreator(Comment foundComment, Protector validProtector) {
        String commentCreatorUsername = foundComment.getProtector().getUsername(); // 댓글 작성자
        Long commentCreatorUserId = foundComment.getProtector().getId();

        String postCreatorUsername = foundComment.getPost().getProtector().getUsername(); // 게시물 작성
        Long postCreatorUserId = foundComment.getPost().getProtector().getId();

        if (!validProtector.getUsername().equals(commentCreatorUsername) && !validProtector.getUsername().equals(postCreatorUsername)){

            List<FcmToken> foundCommentCreatorFcmTokens = fcmTokenRepository.findByProtectorId(commentCreatorUserId);
            List<FcmToken> foundPostCreatorFcmTokens = fcmTokenRepository.findByProtectorId(postCreatorUserId);

            for (FcmToken fcmToken : foundPostCreatorFcmTokens) {
                Message message = Message.builder()
                        .putData("title", "만수무강")
                        .putData("body", "회원님께서 작성하신 게시물에 대댓글이 달렸어요!")
                        .setToken(fcmToken.getFcmToken())
                        .build();

                fcmService.sendMessage(message);
            }

            for (FcmToken fcmToken : foundCommentCreatorFcmTokens) {
                Message message = Message.builder()
                        .putData("title", "만수무강")
                        .putData("body", "회원님의 댓글에 대댓글이 달렸어요!")
                        .setToken(fcmToken.getFcmToken())
                        .build();

                fcmService.sendMessage(message);
            }

        }
    }

}
