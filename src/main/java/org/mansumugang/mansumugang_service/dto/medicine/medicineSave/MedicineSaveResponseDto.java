package org.mansumugang.mansumugang_service.dto.medicine.medicineSave;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MedicineSaveResponseDto {
    private String message;

    public MedicineSaveResponseDto() {
        this.message = "약 정보를 성공적으로 저장하였습니다.";
    }

    public MedicineSaveResponseDto(String message) {
        this.message = message;
    }
}
