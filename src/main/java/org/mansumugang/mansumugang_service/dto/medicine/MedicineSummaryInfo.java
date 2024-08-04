package org.mansumugang.mansumugang_service.dto.medicine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.mansumugang.mansumugang_service.constant.MedicineStatusType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MedicineSummaryInfo {
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
    public static class TimeElement {
        private LocalTime time;
        private List<MedicineSummaryInfoElement> medicines;

        public static TimeElement of(LocalTime time, List<MedicineSummaryInfoElement> medicines) {
            return TimeElement.builder()
                    .time(time)
                    .medicines(medicines)
                    .build();
        }

        public static List<TimeElement> convertTimeElements(Map<LocalTime, List<MedicineSummaryInfoElement>> medicineSummaryInfoByTime) {
            List<org.mansumugang.mansumugang_service.dto.medicine.MedicineSummaryInfo.TimeElement> timeElements = new ArrayList<>();
            medicineSummaryInfoByTime.forEach((localTime, medicineSummaryInfos1) ->
                    timeElements.add(org.mansumugang.mansumugang_service.dto.medicine.MedicineSummaryInfo.TimeElement.of(localTime, medicineSummaryInfos1))
            );

            return timeElements;
        }
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Dto{
        private String imageApiUrlPrefix;
        private LocalDate date;
        private List<TimeElement> medicineSchedules;

        public static Dto of(String imageApiUrlPrefix, LocalDate date, List<TimeElement> medicineSchedules) {
            return Dto.builder()
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
        private String imageApiUrlPrefix;
        private LocalDate date;
        private List<TimeElement> medicineSchedules;

        public static Response fromDto(Dto dto) {
            return Response.builder()
                    .imageApiUrlPrefix(dto.imageApiUrlPrefix)
                    .date(dto.getDate())
                    .medicineSchedules(dto.getMedicineSchedules())
                    .build();
        }
    }
}
