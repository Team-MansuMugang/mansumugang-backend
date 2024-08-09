package org.mansumugang.mansumugang_service.dto.hospital;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class HospitalSave {
    @Getter
    @Setter
    public static class Request {
        @NotNull(message = "환자 아이디를 기입해주세요")
        private Long patientId;

        @Size(min = 2, max = 20, message = "병원 이름의 길이는 2에서 20자 사이여야 합니다")
        @NotNull(message = "병원 이름을 기입해주세요")
        private String hospitalName;

        @Size(min = 2, max = 50, message = "병원 주소의 길이는 2에서 50자 사이여야 합니다")
        @NotNull(message = "병원 주소를 기입해주세요")
        private String hospitalAddress;

        @NotNull(message = "위도를 기입해주세요")
        private Double latitude;

        @NotNull(message = "경도를 기입해주세요")
        private Double longitude;

        @Size(min = 2, max = 200, message = "병원에 대한 설명은 2에서 200자 사이여야 합니다")
        private String hospitalDescription;

        private LocalDateTime hospitalVisitingTime;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response {
        private String message;

        public static Response createNewResponse() {
            return Response.builder()
                    .message("병원 정보를 성공적으로 저장하였습니다.")
                    .build();
        }

        public static Response of(String message) {
            return Response.builder()
                    .message(message)
                    .build();
        }
    }
}
