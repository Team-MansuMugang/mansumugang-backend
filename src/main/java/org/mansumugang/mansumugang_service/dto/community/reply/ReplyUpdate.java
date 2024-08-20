package org.mansumugang.mansumugang_service.dto.community.reply;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
        @NotNull(message = "값이 null이면 안됩니다.")
        private Long replyId; // 수정할 대댓글의 고유번호

        @Valid
        @NotBlank(message = "대댓글은 한글자 이상이어야합니다.")
        private String content; // 수정할 내용


    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Dto{

        private Long replyId;
        private String nickname; // 대댓글 수정자(닉네임)
        private String updatedContent; // 수정된 대댓글 내용
        private LocalDateTime updatedAt; // 대댓글 수정된 시간

        public static Dto of(Reply updatedReply){
            return Dto.builder()
                    .replyId(updatedReply.getId())
                    .nickname(updatedReply.getProtector().getNickname())
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
        private String updatedContent;
        private LocalDateTime updatedAt; // 대댓글 수정된 시간

        public static Response createNewResponse(Dto dto){
            return Response.builder()
                    .message("대댓글이 정상적으로 수정되었습니다!")
                    .replyId(dto.getReplyId())
                    .nickname(dto.getNickname())
                    .updatedContent(dto.getUpdatedContent())
                    .updatedAt(dto.getUpdatedAt())
                    .build();
        }
    }
}
