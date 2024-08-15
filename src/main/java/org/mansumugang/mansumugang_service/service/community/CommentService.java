package org.mansumugang.mansumugang_service.service.community;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mansumugang.mansumugang_service.constant.ErrorType;
import org.mansumugang.mansumugang_service.domain.community.Comment;
import org.mansumugang.mansumugang_service.domain.community.Post;
import org.mansumugang.mansumugang_service.domain.user.Protector;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.dto.community.comment.CommentInquiry;
import org.mansumugang.mansumugang_service.dto.community.comment.CommentSave;
import org.mansumugang.mansumugang_service.exception.CustomErrorException;
import org.mansumugang.mansumugang_service.repository.CommentRepository;
import org.mansumugang.mansumugang_service.repository.PostRepository;
import org.mansumugang.mansumugang_service.repository.ReplyRepository;
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

    private final int COMMENT_PAGE_SIZE = 10; // 한페이지 당 최대 댓글수 : 10개
    private final int REPLY_PAGE_SIZE = 5; //  한페이지 당 최대 대댓글수 : 5개

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

    public CommentInquiry.Response getCommentList(Long cursor, Long postId){

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
        List<CommentInquiry.CommentDto> commentList = commentPage.map(comment1 -> CommentInquiry.CommentDto.of(comment1, replyRepository.findAllByComment(comment1, replyPageable))).toList();

        return CommentInquiry.Response.of(commentList);
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
