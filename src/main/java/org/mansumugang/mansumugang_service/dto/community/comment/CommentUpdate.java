package org.mansumugang.mansumugang_service.dto.community.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.mansumugang.mansumugang_service.domain.community.Comment;

import java.time.LocalDateTime;

public class CommentUpdate {

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class Request{

        @NotNull
        private Long commentId;

        @NotBlank(message = "댓글은 한글자 이상이어야합니다.")
        private String content;
    }


    @Getter
    @AllArgsConstructor
    @Builder
    public static class Dto{
        private Long commentId;
        private String nickname;
        private String beforeContent;
        private String updatedContent;
        private LocalDateTime updatedAt;

        public static Dto fromEntity(String beforeContent, Comment updatedComment){
            return Dto.builder()
                    .commentId(updatedComment.getId())
                    .nickname(updatedComment.getProtector().getNickname())
                    .beforeContent(beforeContent)
                    .updatedContent(updatedComment.getContent())
                    .updatedAt(updatedComment.getUpdatedAt())
                    .build();
        }
    }


    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response{
        private String message;
        private Long commentId;
        private String nickname;
        private String updatedLog;
        private LocalDateTime updatedAt;

        public static Response createNewResponse(Dto dto){
            return Response.builder()
                    .message("댓글이 정상적으로 수정되었습니다!")
                    .commentId(dto.getCommentId())
                    .nickname(dto.getNickname())
                    .updatedLog(dto.getBeforeContent() +" -> " + dto.getUpdatedContent())
                    .updatedAt(dto.getUpdatedAt())
                    .build();
        }
    }
}
