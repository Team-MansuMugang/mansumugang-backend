package org.mansumugang.mansumugang_service.dto.hospital;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class HospitalDelete {
    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response {
        private String message;

        public static Response createNewResponse() {
            return Response.builder()
                    .message("병원 정보를 성공적으로 삭제하였습니다.")
                    .build();
        }

        public static Response of(String message) {
            return Response.builder()
                    .message(message)
                    .build();
        }
    }
}
