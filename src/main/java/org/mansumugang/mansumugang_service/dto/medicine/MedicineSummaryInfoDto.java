package org.mansumugang.mansumugang_service.dto.medicine;


import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalTime;
import java.util.Objects;

@Getter
@Setter
public class MedicineSummaryInfoDto {
        private Boolean status;
        private LocalTime medicineIntakeTime;
        private Long medicineId;
        private String medicineImageName;
        private String hospitalName;
        private String medicineDescription;
        private String medicineName;

        public MedicineSummaryInfoDto(Boolean status,
                                      LocalTime medicineIntakeTime,
                                      Long medicineId,
                                      String medicineImageName,
                                      String hospitalName,
                                      String medicineDescription,
                                      String medicineName) {
            this.status = Objects.requireNonNullElse(status, false);
            this.medicineIntakeTime = medicineIntakeTime;
            this.medicineId = medicineId;
            this.medicineImageName = medicineImageName;
            this.hospitalName = hospitalName;
            this.medicineDescription = medicineDescription;
            this.medicineName = medicineName;
        }
}
