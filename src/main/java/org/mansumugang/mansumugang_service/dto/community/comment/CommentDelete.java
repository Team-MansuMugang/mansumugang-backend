package org.mansumugang.mansumugang_service.dto.community.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.mansumugang.mansumugang_service.domain.community.Comment;

import java.time.LocalDateTime;

public class CommentDelete {

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Dto{
        private Long commentId; // 삭제된 댓글의 아이디
        private Long postId; // 삭제된 댓글의 게시물 아이디
        private LocalDateTime deletedAt; // 대댓글 삭제 시간.

        public static Dto fromEntity(Comment deletedComment){
            return Dto.builder()
                    .commentId(deletedComment.getId())
                    .postId(deletedComment.getPost().getId())
                    .deletedAt(deletedComment.getDeletedAt())
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response{
        private String message;
        private Long commentId; // 삭제된 댓글의 아이디
        private Long postId; // 삭제된 댓글의 게시물 아이디
        private LocalDateTime deletedAt; // 대댓글 삭제 시간.

        public static Response CreateNewResponse(Dto dto){
            return Response.builder()
                    .message("댓글이 정상적으로 삭제되었습니다!")
                    .commentId(dto.getCommentId())
                    .postId(dto.getPostId())
                    .deletedAt(dto.getDeletedAt())
                    .build();
        }
    }
}
