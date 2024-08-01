package org.mansumugang.mansumugang_service.dto.medicine;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class MedicineDelete {

    @Getter
    @Setter
    public static class Request {
        @NotNull(message = "환자 아이디를 기입해주세요")
        private Long patientId;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response {
        private String message;

        public static Response createNewResponse() {
            return Response.builder()
                    .message("약 정보를 성공적으로 삭제하였습니다.")
                    .build();
        }

        public static Response of(String message) {
            return Response.builder()
                    .message(message)
                    .build();
        }
    }
}
