package org.mansumugang.mansumugang_service.dto.user.userProfileImage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class UserProfileImageDelete {
    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response {
        private String message;

        public static Response createNewResponse() {
            return Response.builder()
                    .message("프로필 이미지를 성공적으로 삭제하였습니다.")
                    .build();
        }
    }
}