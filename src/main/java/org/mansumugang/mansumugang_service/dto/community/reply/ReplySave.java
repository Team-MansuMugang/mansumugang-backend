package org.mansumugang.mansumugang_service.dto.community.reply;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.mansumugang.mansumugang_service.domain.community.Reply;

import java.time.LocalDateTime;

public class ReplySave {


    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class Request{

        @NotNull(message = "값이 널이어선 안됩니다.")
        private Long commentId;

        @NotBlank(message = "대댓글은 공백일 수 없습니다.")
        private String content;
    }


    @Getter
    @AllArgsConstructor
    @Builder
    public static class Dto{

        private Long id;
        private String nickname;
        private String content;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static Dto fromEntity(Reply savedReply){
            return Dto.builder()
                    .id(savedReply.getId())
                    .nickname(savedReply.getProtector().getNickname())
                    .content(savedReply.getContent())
                    .createdAt(savedReply.getCreatedAt())
                    .updatedAt(savedReply.getUpdatedAt())
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response{
        private String message;
        private Long id;
        private String nickname;
        private String content;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static Response createNewResponse(Dto dto){
            return Response.builder()
                    .message("대댓글이 저장되었습니다.")
                    .id(dto.getId())
                    .nickname(dto.getNickname())
                    .content(dto.getContent())
                    .createdAt(dto.getCreatedAt())
                    .updatedAt(dto.getUpdatedAt())
                    .build();
        }
    }
}
