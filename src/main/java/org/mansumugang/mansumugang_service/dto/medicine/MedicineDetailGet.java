package org.mansumugang.mansumugang_service.dto.medicine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.mansumugang.mansumugang_service.domain.medicine.Medicine;
import org.mansumugang.mansumugang_service.domain.medicine.MedicineInTakeTime;
import org.mansumugang.mansumugang_service.domain.medicine.MedicineIntakeDay;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class MedicineDetailGet {
    @Getter
    @AllArgsConstructor
    @Builder
    public static class Dto {
        private Long patientId;

        private Long medicineId;

        private String medicineName;

        private String hospitalName;

        private String medicineDescription;

        private List<LocalTime> medicineIntakeTimes;

        private List<DayOfWeek> medicineIntakeDays;

        private LocalDate medicineIntakeStopDay;


        public static Dto of(Medicine medicine, List<MedicineIntakeDay> medicineIntakeDays, List<MedicineInTakeTime> medicineInTakeTimes) {
            return Dto.builder()
                    .patientId(medicine.getPatient().getId())
                    .medicineId(medicine.getId())
                    .medicineName(medicine.getMedicineName())
                    .hospitalName(medicine.getHospitalName())
                    .medicineDescription(medicine.getMedicineDescription())
                    .medicineIntakeTimes(medicineInTakeTimes.stream().map(MedicineInTakeTime::getMedicineIntakeTime).toList())
                    .medicineIntakeDays(medicineIntakeDays.stream().map(MedicineIntakeDay::getDay).toList())
                    .medicineIntakeStopDay(medicine.getIntakeStopDate())
                    .build();
        }
    }


    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long patientId;

        private Long medicineId;

        private String medicineName;

        private String hospitalName;

        private String medicineDescription;

        private List<LocalTime> medicineIntakeTimes;

        private List<DayOfWeek> medicineIntakeDays;

        private LocalDate medicineIntakeStopDay;

        public static Response fromDto(Dto dto) {
            return Response.builder()
                    .patientId(dto.getPatientId())
                    .medicineId(dto.getMedicineId())
                    .medicineName(dto.getMedicineName())
                    .hospitalName(dto.getHospitalName())
                    .medicineDescription(dto.getMedicineDescription())
                    .medicineIntakeTimes(dto.getMedicineIntakeTimes())
                    .medicineIntakeDays(dto.getMedicineIntakeDays())
                    .medicineIntakeStopDay(dto.getMedicineIntakeStopDay())
                    .build();
        }
    }
}
