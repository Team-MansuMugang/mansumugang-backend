package org.mansumugang.mansumugang_service.service.community;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mansumugang.mansumugang_service.constant.ErrorType;
import org.mansumugang.mansumugang_service.domain.community.Comment;
import org.mansumugang.mansumugang_service.domain.community.Post;
import org.mansumugang.mansumugang_service.domain.community.Reply;
import org.mansumugang.mansumugang_service.domain.user.Protector;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.dto.community.comment.CommentSave;
import org.mansumugang.mansumugang_service.dto.community.reply.ReplySave;
import org.mansumugang.mansumugang_service.exception.CustomErrorException;
import org.mansumugang.mansumugang_service.repository.CommentRepository;
import org.mansumugang.mansumugang_service.repository.PostRepository;
import org.mansumugang.mansumugang_service.repository.ReplyRepository;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReplyService {

    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;

    @Transactional
    public ReplySave.Dto saveReply(User user, ReplySave.Request request){

        // 1. user가 보호자 객체인지 검증
        Protector validProtector = validateProtector(user);

        // 2. 넘겨받은 postId로 게시물 조회 -> 없으면 예외처리.
        Comment foundComment = commentRepository.findById(request.getCommentId()).orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchCommentError));

        // 3. 찾은 게시물에 댓글 저장.
        Reply savedReply = replyRepository.save(Reply.of(request, foundComment, validProtector));

        return ReplySave.Dto.fromEntity(savedReply);

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

}
