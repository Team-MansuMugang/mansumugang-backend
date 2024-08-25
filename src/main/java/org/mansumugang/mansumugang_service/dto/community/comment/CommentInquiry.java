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

        private String profileImageName; // 댓글 작성자의 프로필 파일 이미지 이름.

        private String creator; // 댓글 작성자(닉네임)

        private String content; // 댓글 내용

        private LocalDateTime createdAt; // 댓글 생성시간

        private LocalDateTime updatedAt; // 댓글 업데이트 시간

        private LocalDateTime deletedAt; // 댓글 삭제시간

        // 프로필 파일이 설정되었고, deleteAt이 null 이 아니면 프로필 이미지 이름 노출, 프로필 이미지가 null 이고 deletedAt이 null 이면 null 로 설정
        public static CommentElement fromEntity(Comment comment) {
            return CommentElement.builder()
                    .commentId(comment.getId())
                    .postId(comment.getPost().getId())
                    .profileImageName(comment.getProtector().getProfileImageName() != null && comment.getDeletedAt() == null ? comment.getProtector().getProfileImageName() : null)
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
