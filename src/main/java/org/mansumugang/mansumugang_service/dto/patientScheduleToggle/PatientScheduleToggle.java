package org.mansumugang.mansumugang_service.dto.patientScheduleToggle;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.mansumugang.mansumugang_service.constant.MedicineRecordStatusType;
import org.mansumugang.mansumugang_service.constant.ScheduleToggleType;
import org.mansumugang.mansumugang_service.domain.hospital.Hospital;
import org.mansumugang.mansumugang_service.domain.medicine.MedicineIntakeRecord;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class PatientScheduleToggle {
    @Getter
    @Setter
    public static class Request {
        @Valid
        private MedicineRequest medicine;

        private Long hospitalId;
    }

    @Getter
    @Setter
    public static class MedicineRequest{
        @NotNull(message = "하나 이상의 약 아이디가 필요합니다.")
        private List<Long> medicineIds;

        @NotNull(message = "약 복용시간이 필요합니다.")
        private LocalTime medicineIntakeTime;

        @NotNull(message = "약 복용 일자가 필요합니다.")
        private LocalDate scheduledMedicineIntakeDate;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class ToggleResultElement {
        private ScheduleToggleType type;

        private Long id;

        private Boolean status;

        private LocalDateTime updatedTime;

        public static ToggleResultElement fromEntity(MedicineIntakeRecord medicineIntakeRecord){
            return ToggleResultElement.builder()
                    .type(ScheduleToggleType.MEDICINE)
                    .id(medicineIntakeRecord.getMedicine().getId())
                    .status(medicineIntakeRecord.getStatus() == MedicineRecordStatusType.TRUE)
                    .updatedTime(medicineIntakeRecord.getActualIntakeTime())
                    .build();
        }

        public static ToggleResultElement fromEntity(Hospital hospital){
            return ToggleResultElement.builder()
                    .type(ScheduleToggleType.HOSPITAL)
                    .id(hospital.getId())
                    .status(hospital.getStatus())
                    .updatedTime(hospital.getActualHospitalVisitingTime())
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
        List<ToggleResultElement> toggleResult;

        public static Response fromDto(Dto dto){
            return Response.builder()
                    .toggleResult(dto.getToggleResult())
                    .build();
        }
    }

}
