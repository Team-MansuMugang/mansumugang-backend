package org.mansumugang.mansumugang_service.dto.medicineIntake;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.mansumugang.mansumugang_service.constant.MedicineRecordStatusType;
import org.mansumugang.mansumugang_service.domain.medicine.MedicineIntakeRecord;
import org.mansumugang.mansumugang_service.dto.auth.signup.SignUpDto;
import org.mansumugang.mansumugang_service.dto.auth.signup.SignUpResponseDto;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class MedicineIntakeToggleDto {
    private MedicineRecordStatusType status;

    private LocalDateTime actualIntakeTime;


    public static MedicineIntakeToggleDto fromEntity(MedicineIntakeRecord medicineIntakeRecord){
        return MedicineIntakeToggleDto.builder()
                .status(medicineIntakeRecord.getStatus())
                .actualIntakeTime(medicineIntakeRecord.getActualIntakeTime())
                .build();
    }
}
