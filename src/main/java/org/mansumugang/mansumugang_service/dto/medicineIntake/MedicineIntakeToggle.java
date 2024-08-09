package org.mansumugang.mansumugang_service.dto.medicineIntake;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.mansumugang.mansumugang_service.constant.MedicineRecordStatusType;
import org.mansumugang.mansumugang_service.domain.medicine.MedicineIntakeRecord;
import org.mansumugang.mansumugang_service.validation.ValidDate;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class MedicineIntakeToggle {
    @Getter
    @Setter
    public static class Request {
        @NotNull(message = "하나 이상의 약 아이디가 필요합니다.")
        private List<Long> medicineIds;

        @NotNull(message = "약 복용시간이 필요합니다.")
        private LocalTime medicineIntakeTime;

        @ValidDate
        @NotNull(message = "약 복용 일자가 필요합니다.")
        private String scheduledMedicineIntakeDate;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class ToggleResultElement {
        private MedicineRecordStatusType status;

        private LocalDateTime actualIntakeTime;

        public static ToggleResultElement fromEntity(MedicineIntakeRecord medicineIntakeRecord){
            return ToggleResultElement.builder()
                    .status(medicineIntakeRecord.getStatus())
                    .actualIntakeTime(medicineIntakeRecord.getActualIntakeTime())
                    .build();
        }
    }


    @Getter
    @AllArgsConstructor
    @Builder
    public static class Dto {
        List<ToggleResultElement> toggleResult;

        public static Dto fromElement(List<ToggleResultElement> toggleResult){
            return Dto.builder()
                    .toggleResult(toggleResult)
                    .build();
        }
    }



    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response {
        List<ToggleResultElement> toggleResponses;

        public static Response fromDto(Dto dto){
            return Response.builder()
                    .toggleResponses(dto.getToggleResult())
                    .build();
        }
    }

}
