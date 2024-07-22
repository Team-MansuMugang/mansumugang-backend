package org.mansumugang.mansumugang_service.dto.medicine.medicineSave;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.mansumugang.mansumugang_service.dto.medicine.common.MedicineIntakeDayDto;
import org.mansumugang.mansumugang_service.dto.medicine.common.MedicineIntakeTimeDto;
import org.mansumugang.mansumugang_service.validation.ValidDate;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class MedicineSaveRequestDto {
    @NotNull(message = "환자 아이디를 기입해주세요")
    private Long patientId;

    @Size(min = 2, max = 20, message = "약 이름의 길이는 2에서 20자 사이여야 합니다")
    @NotNull(message = "약 이름을 기입해주세요")
    private String medicineName;

    @Size(min = 2, max = 20, message = "병원 이름의 길이는 2에서 20자 사이여야 합니다")
    private String hospitalName;

    @Size(min = 2, max = 200, message = "약에 대한 설명은 2에서 200자 사이여야 합니다")
    private String medicineDescription;

    @Valid
    @NotNull(message = "약 복용시간이 필요합니다.")
    private List<MedicineIntakeTimeDto> medicineIntakeTimes;

    @Valid
    @NotNull(message = "약 복용요일이 필요합니다.")
    private MedicineIntakeDayDto medicineIntakeDays;

    @ValidDate(message = "유효하지 않은 날짜 형식입니다.")
    @NotNull(message = "약 섭취 종료 일자가 필요합니다")
    private String medicineIntakeStopDay;
}
