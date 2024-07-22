package org.mansumugang.mansumugang_service.dto.medicine.common;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MedicineIntakeTimeDto {
    @Min(value = 0, message = "약 복용시간의 시간은 0이상여야 합니다.")
    @Max(value = 23, message = "약 복용시간의 시간은 23이하여야 합니다.")
    @NotNull(message = "약 섭취 시간이 필요합니다")
    private int medicineIntakeHours;

    @Min(value = 0, message = "약 복용시간의 분은 0이상여야 합니다.")
    @Max(value = 60, message = "약 복용시간의 분은 60이하여야 합니다.")
    @NotNull(message = "약 섭취 시간이 필요합니다")
    private int medicineIntakeMinutes;
}
