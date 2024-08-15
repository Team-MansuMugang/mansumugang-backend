package org.mansumugang.mansumugang_service.dto.fcm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class FcmMessage {


    @Builder
    @AllArgsConstructor
    @Getter
    public static class Response{
        private String message;

        public static Response createNewResponse(){
            return Response.builder()
                    .message("메시지 전송 완료")
                    .build();
        }
    }
}
