package org.mansumugang.mansumugang_service.dto.community.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.mansumugang.mansumugang_service.domain.community.Comment;
import org.mansumugang.mansumugang_service.domain.community.Reply;
import org.mansumugang.mansumugang_service.dto.community.reply.ReplyInquiry;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public class CommentInquiry {

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class CommentElement{
        private Long commentId; // 댓글 고유번호

        private Long postId; // 게시물 고유번호

        private String creator; // 댓글 작성자(닉네임)

        private String content; // 댓글 내용

        private LocalDateTime createdAt; // 댓글 생성시간

        private LocalDateTime updatedAt; // 댓글 업데이트 시간

        private LocalDateTime deletedAt; // 댓글 삭제시간

        public static CommentElement fromEntity(Comment comment) {
            return CommentElement.builder()
                    .commentId(comment.getId())
                    .postId(comment.getPost().getId())
                    .creator(comment.getDeletedAt() == null ? comment.getProtector().getNickname() : "알 수 없음")
                    .content(comment.getContent())
                    .createdAt(comment.getCreatedAt())
                    .updatedAt(comment.getUpdatedAt())
                    .deletedAt(comment.getDeletedAt())
                    .build();
        }
    }


    @Getter
    @AllArgsConstructor
    @Builder
    public static class CommentDto{
        private CommentElement comment;
        private ReplyInquiry.Response reply;

        public static CommentDto of(Comment comment, Page<Reply> replyPage){
            return CommentDto.builder()
                    .comment(CommentElement.fromEntity(comment))
                    .reply(ReplyInquiry.Response.fromPage(replyPage))
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response{
        private List<CommentDto> comments;

        public static Response of(List<CommentDto> comments){
            return Response.builder()
                    .comments(comments)
                    .build();
        }

    }

}
