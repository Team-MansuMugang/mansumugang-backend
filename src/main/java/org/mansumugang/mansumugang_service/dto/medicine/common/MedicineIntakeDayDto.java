package org.mansumugang.mansumugang_service.dto.medicine.common;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MedicineIntakeDayDto {
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