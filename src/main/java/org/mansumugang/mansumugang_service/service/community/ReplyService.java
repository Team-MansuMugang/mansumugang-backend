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
import org.mansumugang.mansumugang_service.service.user.UserCommonService;
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
    private final UserCommonService userCommonService;

    private final int REPLY_PAGE_SIZE = 5; // 한페이지당 대댓글 수 : 5

    @Value("${file.upload.image.api}")
    private String imageApiUrl;

    @Transactional
    public ReplySave.Dto saveReply(User user, ReplySave.Request request){

        Protector validProtector = userCommonService.findProtector(user);

        Comment foundComment = commentRepository.findById(request.getCommentId()).orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchCommentError));

        Reply savedReply = replyRepository.save(Reply.of(request, foundComment, validProtector));

        sendMessageToPostAndCommentCreator(foundComment, validProtector);

        return ReplySave.Dto.fromEntity(savedReply);

    }

    public ReplyInquiry.Response getReplyList(User user, Long cursor, Long commentId){

        userCommonService.findProtector(user);

        Comment foundComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchCommentError));

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

        Protector validProtector = userCommonService.findProtector(user);

        Reply foundReply = replyRepository.findById(request.getReplyId())
                .orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchReplyError));

        if (foundReply.getDeletedAt() != null){
            throw new CustomErrorException(ErrorType.DeletedReplyError);
        }

        if (!foundReply.getProtector().getUsername().equals(validProtector.getUsername())){
            throw new CustomErrorException(ErrorType.NotTheAuthorOfTheReply);
        }

        foundReply.update(request.getContent());

        return ReplyUpdate.Dto.of(foundReply);
    }

    @Transactional
    public ReplyDelete.Dto deleteReply(User user, Long replyId){

        Protector validProtector = userCommonService.findProtector(user);

        Reply foundReply = replyRepository.findById(replyId)
                .orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchReplyError));

        if (foundReply.getDeletedAt() != null){
            throw new CustomErrorException(ErrorType.DeletedReplyError);
        }

        if (!foundReply.getProtector().getUsername().equals(validProtector.getUsername())){
            throw new CustomErrorException(ErrorType.NotTheAuthorOfTheReply);
        }

        foundReply.delete();

        return ReplyDelete.Dto.fromEntity(foundReply);
    }


    private void sendMessageToPostAndCommentCreator(Comment foundComment, Protector validProtector) {
        String commentCreatorUsername = foundComment.getProtector().getUsername();
        Long commentCreatorUserId = foundComment.getProtector().getId();

        String postCreatorUsername = foundComment.getPost().getProtector().getUsername();
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
