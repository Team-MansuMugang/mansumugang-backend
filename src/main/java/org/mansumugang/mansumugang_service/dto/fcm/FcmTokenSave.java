package org.mansumugang.mansumugang_service.dto.fcm;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.mansumugang.mansumugang_service.domain.user.Protector;

public class FcmTokenSave {

    @Getter
    @Setter
    @Builder
    public static class Request{

        @NotNull
        private String fcmToken;

        @NotNull
        private Protector protector;

    }

    @Builder
    @Getter
    @AllArgsConstructor
    public static class Response{
        private String message;

        public static Response createNewResponse(){
            return Response.builder()
                    .message("FCM 토큰이 저장완료되었습니다.")
                    .build();
        }
    }


}
