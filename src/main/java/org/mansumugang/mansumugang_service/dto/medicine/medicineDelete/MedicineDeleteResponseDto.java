package org.mansumugang.mansumugang_service.dto.medicine.medicineDelete;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MedicineDeleteResponseDto {
    private String message;

    public MedicineDeleteResponseDto() {
        this.message = "약 정보를 성공적으로 삭제하였습니다.";
    }

    public MedicineDeleteResponseDto(String message) {
        this.message = message;
    }
}
