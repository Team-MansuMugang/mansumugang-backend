package org.mansumugang.mansumugang_service.dto.medicine;


import lombok.Getter;
import lombok.Setter;
import org.mansumugang.mansumugang_service.constant.MedicineRecordStatusType;
import org.mansumugang.mansumugang_service.constant.MedicineStatusType;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalTime;
import java.util.Objects;

@Getter
@Setter
public class MedicineSummaryInfoResult {
        private MedicineRecordStatusType status;
        private LocalTime medicineIntakeTime;
        private Long medicineId;
        private String medicineImageName;
        private String hospitalName;
        private String medicineDescription;
        private String medicineName;

        public MedicineSummaryInfoResult(MedicineRecordStatusType status,
                                      LocalTime medicineIntakeTime,
                                      Long medicineId,
                                      String medicineImageName,
                                      String hospitalName,
                                      String medicineDescription,
                                      String medicineName) {
            this.status = status;
            this.medicineIntakeTime = medicineIntakeTime;
            this.medicineId = medicineId;
            this.medicineImageName = medicineImageName;
            this.hospitalName = hospitalName;
            this.medicineDescription = medicineDescription;
            this.medicineName = medicineName;
        }
}
