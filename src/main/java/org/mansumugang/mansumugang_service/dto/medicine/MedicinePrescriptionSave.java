package org.mansumugang.mansumugang_service.dto.medicine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class MedicinePrescriptionSave {
    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response {
        private String message;

        public static Response createNewResponse() {
            return Response.builder()
                    .message("약 처방전 정보를 성공적으로 저장하였습니다.")
                    .build();
        }

        public static Response of(String message) {
            return Response.builder()
                    .message(message)
                    .build();
        }
    }

}
