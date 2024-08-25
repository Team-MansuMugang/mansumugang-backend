package org.mansumugang.mansumugang_service.dto.community.reply;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.mansumugang.mansumugang_service.domain.community.Reply;

import java.time.LocalDateTime;

public class ReplyDelete {

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Dto{

        private Long replyId; // 삭제된 대댓글의 아이디
        private Long commentId;
        private LocalDateTime deletedAt; // 대댓글 삭제 시간.

        public static Dto fromEntity(Reply deletedReply){
            return Dto.builder()
                    .replyId(deletedReply.getId())
                    .commentId(deletedReply.getComment().getId())
                    .deletedAt(deletedReply.getDeletedAt())
                    .build();
        }
    }


    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response{

        private String message;
        private Long replyId; // 삭제된 대댓글의 아이디
        private Long commentId;
        private LocalDateTime deletedAt; // 대댓글 삭제 시간.

        public static Response createNewResponse(Dto dto){
            return Response.builder()
                    .message("대댓글이 정상적으로 삭제되었습니다.")
                    .replyId(dto.getReplyId())
                    .commentId(dto.getCommentId())
                    .deletedAt(dto.getDeletedAt())
                    .build();

        }


    }
}
