package org.mansumugang.mansumugang_service.dto.medicineIntake;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.mansumugang.mansumugang_service.constant.MedicineRecordStatusType;
import org.mansumugang.mansumugang_service.domain.medicine.MedicineIntakeRecord;
import org.mansumugang.mansumugang_service.dto.auth.signup.SignUpDto;
import org.mansumugang.mansumugang_service.dto.auth.signup.SignUpResponseDto;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class MedicineIntakeToggleResponseDto {
    private MedicineRecordStatusType status;

    private LocalDateTime actualIntakeTime;


    public static MedicineIntakeToggleResponseDto dtoToResponse(MedicineIntakeToggleDto medicineIntakeToggleDto){
        return MedicineIntakeToggleResponseDto.builder()
                .status(medicineIntakeToggleDto.getStatus())
                .actualIntakeTime(medicineIntakeToggleDto.getActualIntakeTime())
                .build();
    }
}
