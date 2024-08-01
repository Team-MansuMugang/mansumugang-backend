package org.mansumugang.mansumugang_service.dto.medicine;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.mansumugang.mansumugang_service.validation.ValidDate;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

public class MedicineUpdate {
    @Getter
    @Setter
    public static class Request {
        @NotNull(message = "환자 아이디를 기입해주세요")
        private Long patientId;

        @Size(min = 2, max = 20, message = "약 이름의 길이는 2에서 20자 사이여야 합니다")
        private String medicineName;

        @Size(min = 2, max = 20, message = "병원 이름의 길이는 2에서 20자 사이여야 합니다")
        private String hospitalName;

        @Size(min = 2, max = 200, message = "약에 대한 설명은 2에서 200자 사이여야 합니다")
        private String medicineDescription;

        private List<LocalTime> medicineIntakeTimeToAdd;

        private List<LocalTime> medicineIntakeTimeToDelete;

        private List<DayOfWeek> medicineIntakeDayToAdd;

        private List<DayOfWeek> medicineIntakeDayToDelete;

        @ValidDate(message = "유효하지 않은 날짜 형식입니다.")
        private String medicineIntakeStopDay;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response {
        private String message;

        public static Response createNewResponse() {
            return Response.builder()
                    .message("약 정보를 성공적으로 수정하였습니다.")
                    .build();
        }

        public static Response of(String message) {
            return Response.builder()
                    .message(message)
                    .build();
        }
    }
}
