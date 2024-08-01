package org.mansumugang.mansumugang_service.dto.medicine;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.mansumugang.mansumugang_service.validation.ValidDate;

import java.time.LocalTime;
import java.util.List;

public class MedicineSave{
    @Getter
    @Setter
    public static class Request {
        @NotNull(message = "환자 아이디를 기입해주세요")
        private Long patientId;

        @Size(min = 2, max = 20, message = "약 이름의 길이는 2에서 20자 사이여야 합니다")
        @NotNull(message = "약 이름을 기입해주세요")
        private String medicineName;

        @Size(min = 2, max = 20, message = "병원 이름의 길이는 2에서 20자 사이여야 합니다")
        private String hospitalName;

        @Size(min = 2, max = 200, message = "약에 대한 설명은 2에서 200자 사이여야 합니다")
        private String medicineDescription;

        @Valid
        @NotNull(message = "약 복용시간이 필요합니다.")
        private List<LocalTime> medicineIntakeTimes;

        @Valid
        @NotNull(message = "약 복용요일이 필요합니다.")
        private MedicineIntakeDay medicineIntakeDays;

        @ValidDate(message = "유효하지 않은 날짜 형식입니다.")
        @NotNull(message = "약 섭취 종료 일자가 필요합니다")
        private String medicineIntakeStopDay;
    }

    @Getter
    @Setter
    public static class MedicineIntakeDay {
        @NotNull(message = "요일이 필요합니다.")
        private Boolean monday;

        @NotNull(message = "요일이 필요합니다.")
        private Boolean tuesday;

        @NotNull(message = "요일이 필요합니다.")
        private Boolean wednesday;

        @NotNull(message = "요일이 필요합니다.")
        private Boolean thursday;

        @NotNull(message = "요일이 필요합니다.")
        private Boolean friday;

        @NotNull(message = "요일이 필요합니다.")
        private Boolean saturday;

        @NotNull(message = "요일이 필요합니다.")
        private Boolean sunday;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response {
        private String message;

        public static Response createNewResponse() {
            return Response.builder()
                    .message("약 정보를 성공적으로 저장하였습니다.")
                    .build();
        }

        public static Response of(String message) {
            return Response.builder()
                    .message(message)
                    .build();
        }
    }


}