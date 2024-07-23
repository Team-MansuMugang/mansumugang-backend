package org.mansumugang.mansumugang_service.dto.medicineIntake;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.mansumugang.mansumugang_service.dto.medicine.common.MedicineIntakeTimeDto;
import org.mansumugang.mansumugang_service.validation.ValidDate;

@Getter
@Setter
public class MedicineIntakeToggleRequestDto {
    @NotNull(message = "약 아이디가 필요합니다.")
    private Long medicineId;

    @NotNull(message = "약 복용시간이 필요합니다.")
    private MedicineIntakeTimeDto medicineIntakeTime;

    @ValidDate
    @NotNull(message = "약 복용 일자가 필요합니다.")
    private String scheduledMedicineIntakeDate;
}
