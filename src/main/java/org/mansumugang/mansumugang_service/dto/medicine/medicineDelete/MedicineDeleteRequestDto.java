package org.mansumugang.mansumugang_service.dto.medicine.medicineDelete;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MedicineDeleteRequestDto {
    @NotNull(message = "환자 아이디를 기입해주세요")
    private Long patientId;
}
