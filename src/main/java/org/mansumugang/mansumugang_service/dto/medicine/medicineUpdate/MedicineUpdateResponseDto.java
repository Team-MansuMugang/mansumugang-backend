package org.mansumugang.mansumugang_service.dto.medicine.medicineUpdate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MedicineUpdateResponseDto {
    private String message;

    public MedicineUpdateResponseDto() {
        this.message = "약 정보를 성공적으로 수정하였습니다.";
    }

    public MedicineUpdateResponseDto(String message) {
        this.message = message;
    }
}
