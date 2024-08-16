package org.mansumugang.mansumugang_service.dto.community.reply;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.mansumugang.mansumugang_service.domain.community.Reply;

import java.time.LocalDateTime;

public class ReplyUpdate {

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class Request{

        @Valid
        @NotNull
        private Long replyId; // 수정할 대댓글의 고유번호

        @Valid
        @NotNull
        private String content; // 수정할 내용


    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Dto{

        private Long replyId;
        private String nickname; // 대댓글 수정자(닉네임)
        private String beforeContent; // 수정전 대댓글 내용
        private String updatedContent; // 수정된 대댓글 내용
        private LocalDateTime updatedAt; // 대댓글 수정된 시간

        public static Dto of(String beforeContent, Reply updatedReply){
            return Dto.builder()
                    .replyId(updatedReply.getId())
                    .nickname(updatedReply.getProtector().getNickname())
                    .beforeContent(beforeContent)
                    .updatedContent(updatedReply.getContent())
                    .updatedAt(updatedReply.getUpdatedAt())
                    .build();
        }
    }


    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response{
        private String message;
        private Long replyId;
        private String nickname; // 대댓글 수정자(닉네임)
        private String updatedLog;
        private LocalDateTime updatedAt; // 대댓글 수정된 시간

        public static Response createNewResponse(Dto dto){
            return Response.builder()
                    .message("대댓글이 정상적으로 수정되었습니다!")
                    .replyId(dto.getReplyId())
                    .nickname(dto.getNickname())
                    .updatedLog(dto.getBeforeContent() + " -> " + dto.getUpdatedContent())
                    .updatedAt(dto.getUpdatedAt())
                    .build();
        }
    }
}
