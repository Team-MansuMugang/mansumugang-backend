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
        private Long commentId;

        private Long postId;

        private String profileImageName;

        private String creator;

        private String content;

        private LocalDateTime createdAt;

        private LocalDateTime updatedAt;

        private LocalDateTime deletedAt;


        public static CommentElement fromEntity(Comment comment) {
            return CommentElement.builder()
                    .commentId(comment.getId())
                    .postId(comment.getPost().getId())
                    .profileImageName(comment.getProtector() != null && comment.getProtector().getProfileImageName() != null && comment.getDeletedAt() == null ? comment.getProtector().getProfileImageName() : null)
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

        public static CommentDto of(Comment comment, Page<Reply> replyPage, String imageApiUrl){
            return CommentDto.builder()
                    .comment(CommentElement.fromEntity(comment))
                    .reply(ReplyInquiry.Response.fromPage(replyPage, imageApiUrl))
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response{
        private String imageApiUrl;
        private List<CommentDto> comments;

        public static Response of(List<CommentDto> comments, String imageApiUrl){
            return Response.builder()
                    .imageApiUrl(imageApiUrl)
                    .comments(comments)
                    .build();
        }

    }

}
