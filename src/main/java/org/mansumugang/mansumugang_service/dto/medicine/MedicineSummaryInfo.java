package org.mansumugang.mansumugang_service.dto.medicine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.mansumugang.mansumugang_service.constant.MedicineStatusType;
import org.mansumugang.mansumugang_service.domain.hospital.Hospital;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MedicineSummaryInfo {
    @Getter
    @AllArgsConstructor
    @Builder
    public static class HospitalSummaryInfoElement {
        private Long hospitalId;
        private String hospitalName;
        private String hospitalAddress;
        private Double latitude;
        private Double longitude;
        private String hospitalDescription;
        private Boolean status;

        public static HospitalSummaryInfoElement fromEntity(Hospital hospital) {
            return HospitalSummaryInfoElement.builder()
                    .hospitalId(hospital.getId())
                    .hospitalName(hospital.getHospitalName())
                    .hospitalAddress(hospital.getHospitalAddress())
                    .latitude(hospital.getLatitude())
                    .longitude(hospital.getLongitude())
                    .hospitalDescription(hospital.getHospitalDescription())
                    .status(hospital.getStatus())
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class MedicineSummaryInfoElement {
        private Long medicineId;
        private String medicineName;
        private String medicineImageName;
        private String hospitalName;
        private String medicineDescription;
        private LocalTime medicineIntakeTime;
        private MedicineStatusType status;

        public static MedicineSummaryInfoElement of(MedicineSummaryInfoResult medicineSummaryInfoResult, MedicineStatusType status) {
            return MedicineSummaryInfoElement.builder()
                    .medicineId(medicineSummaryInfoResult.getMedicineId())
                    .medicineName(medicineSummaryInfoResult.getMedicineName())
                    .medicineImageName(medicineSummaryInfoResult.getMedicineImageName())
                    .hospitalName(medicineSummaryInfoResult.getHospitalName())
                    .medicineDescription(medicineSummaryInfoResult.getMedicineDescription())
                    .medicineIntakeTime(medicineSummaryInfoResult.getMedicineIntakeTime())
                    .status(status)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class FilteredMedicineSummaryInfoElement {
        private Long medicineId;
        private String medicineName;
        private String medicineImageName;
        private String hospitalName;
        private String medicineDescription;
        private MedicineStatusType status;

        public static FilteredMedicineSummaryInfoElement of(MedicineSummaryInfoElement medicineSummaryInfoElement) {
            return FilteredMedicineSummaryInfoElement.builder()
                    .medicineId(medicineSummaryInfoElement.getMedicineId())
                    .medicineName(medicineSummaryInfoElement.getMedicineName())
                    .medicineImageName(medicineSummaryInfoElement.getMedicineImageName())
                    .hospitalName(medicineSummaryInfoElement.getHospitalName())
                    .medicineDescription(medicineSummaryInfoElement.getMedicineDescription())
                    .status(medicineSummaryInfoElement.getStatus())
                    .build();
        }

        public static List<FilteredMedicineSummaryInfoElement> convertFilteredMedicineSummaryInfoElement(List<MedicineSummaryInfo.MedicineSummaryInfoElement> medicineSummaryInfos) {
            return medicineSummaryInfos.stream().map(FilteredMedicineSummaryInfoElement::of).toList();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class ScheduleElement{
        private List<MedicineSummaryInfoElement> medicines;
        private Hospital hospital;
    }


    @Getter
    @AllArgsConstructor
    @Builder
    public static class TimeElement {
        private String time;
        private List<FilteredMedicineSummaryInfoElement> medicines;
        private HospitalSummaryInfoElement hospital;

        public static TimeElement of(LocalTime time, ScheduleElement scheduleElement) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            return TimeElement.builder()
                    .time(time.format(formatter))
                    .medicines(FilteredMedicineSummaryInfoElement.convertFilteredMedicineSummaryInfoElement(scheduleElement.getMedicines()))
                    .hospital(scheduleElement.getHospital() == null ? null : HospitalSummaryInfoElement.fromEntity(scheduleElement.getHospital()))
                    .build();
        }

        public static List<TimeElement> convertTimeElements(Map<LocalTime, ScheduleElement> scheduleElementMap) {
            List<TimeElement> timeElements = new ArrayList<>();
            scheduleElementMap.forEach((localTime, scheduleElement) -> timeElements.add(TimeElement.of(localTime, scheduleElement)));

            return timeElements;
        }

    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Dto{
        private Long patientId;
        private String imageApiUrlPrefix;
        private LocalDate date;
        private List<TimeElement> medicineSchedules;

        public static Dto of(String imageApiUrlPrefix, LocalDate date, List<TimeElement> medicineSchedules, Long patientId) {
            return Dto.builder()
                    .patientId(patientId)
                    .imageApiUrlPrefix(imageApiUrlPrefix)
                    .date(date)
                    .medicineSchedules(medicineSchedules)
                    .build();
        }


    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response{
        private Long patientId;
        private String imageApiUrlPrefix;
        private LocalDate date;
        private List<TimeElement> medicineSchedules;

        public static Response fromDto(Dto dto) {
            return Response.builder()
                    .patientId(dto.getPatientId())
                    .imageApiUrlPrefix(dto.imageApiUrlPrefix)
                    .date(dto.getDate())
                    .medicineSchedules(dto.getMedicineSchedules())
                    .build();
        }
    }
}
