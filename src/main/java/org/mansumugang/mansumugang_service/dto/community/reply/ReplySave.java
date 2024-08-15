package org.mansumugang.mansumugang_service.dto.community.reply;

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

        @NotNull
        private Long commentId; // 대댓글 고유번호

        @NotNull
        private String content; // 대댓글 내용
    }


    @Getter
    @AllArgsConstructor
    @Builder
    public static class Dto{

        private Long id; // 댓글 아이디
        private String nickname; // 대댓글 작성자 닉네임
        private String content; // 대댓글 내용
        private LocalDateTime createdAt; // 댓글 작성시간
        private LocalDateTime updatedAt; // 댓글 업데이트 시간

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
        private Long id; // 댓글 아이디
        private String nickname; // 대댓글 작성자 닉네임
        private String content; // 대댓글 내용
        private LocalDateTime createdAt; // 댓글 작성시간
        private LocalDateTime updatedAt; // 댓글 업데이트 시간

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
