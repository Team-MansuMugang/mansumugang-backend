package org.mansumugang.mansumugang_service.service.community;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mansumugang.mansumugang_service.constant.ErrorType;
import org.mansumugang.mansumugang_service.domain.community.Comment;
import org.mansumugang.mansumugang_service.domain.community.Post;
import org.mansumugang.mansumugang_service.domain.user.Protector;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.dto.community.comment.CommentSave;
import org.mansumugang.mansumugang_service.exception.CustomErrorException;
import org.mansumugang.mansumugang_service.repository.CommentRepository;
import org.mansumugang.mansumugang_service.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public CommentSave.Dto saveComment(User user, CommentSave.Request request){

        // 1. user가 보호자 객체인지 검증
        Protector validProtector = validateProtector(user);

        // 2. 넘겨받은 postId로 게시물 조회 -> 없으면 예외처리.
        Post foundPost = postRepository.findById(request.getPostId()).orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchPostError));

        // 3. 찾은 게시물에 댓글 저장.
        Comment savedComment = commentRepository.save(Comment.of(request, foundPost, validProtector));

        return CommentSave.Dto.fromEntity(savedComment);

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
