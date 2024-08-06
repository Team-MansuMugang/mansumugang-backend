package org.mansumugang.mansumugang_service.dto.medicine;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.mansumugang.mansumugang_service.constant.MedicineRecordStatusType;
import org.mansumugang.mansumugang_service.domain.medicine.Medicine;
import org.mansumugang.mansumugang_service.domain.medicine.MedicineInTakeTime;
import org.mansumugang.mansumugang_service.domain.medicine.MedicineIntakeRecord;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
public class TodayMedicineScheduleResult {
        private LocalDate targetDate;
        private Medicine medicine;
        private MedicineIntakeRecord medicineIntakeRecord;
        private MedicineInTakeTime medicineInTakeTime;
}
