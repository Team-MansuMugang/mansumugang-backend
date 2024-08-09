package org.mansumugang.mansumugang_service.dto.hospital;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.mansumugang.mansumugang_service.domain.hospital.Hospital;

import java.time.LocalDateTime;

public class HospitalDetailGet {
    @Getter
    @AllArgsConstructor
    @Builder
    public static class Dto {
        private Long hospitalId;

        private Long patientId;

        private String hospitalName;

        private String hospitalAddress;

        private Double latitude;

        private Double longitude;

        private String hospitalDescription;

        private LocalDateTime hospitalVisitingTime;

        public static Dto fromEntity(Hospital hospital){
            return Dto.builder()
                    .hospitalId(hospital.getId())
                    .patientId(hospital.getPatient().getId())
                    .hospitalName(hospital.getHospitalName())
                    .hospitalAddress(hospital.getHospitalAddress())
                    .latitude(hospital.getLatitude())
                    .longitude(hospital.getLongitude())
                    .hospitalDescription(hospital.getHospitalDescription())
                    .hospitalVisitingTime(hospital.getHospitalVisitingTime())
                    .build();
        }
    }


    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long hospitalId;

        private Long patientId;

        private String hospitalName;

        private String hospitalAddress;

        private Double latitude;

        private Double longitude;

        private String hospitalDescription;

        private LocalDateTime hospitalVisitingTime;

        public static Response fromDto(Dto dto){
            return Response.builder()
                    .hospitalId(dto.getHospitalId())
                    .patientId(dto.getPatientId())
                    .hospitalName(dto.getHospitalName())
                    .hospitalAddress(dto.getHospitalAddress())
                    .latitude(dto.getLatitude())
                    .longitude(dto.getLongitude())
                    .hospitalDescription(dto.getHospitalDescription())
                    .hospitalVisitingTime(dto.getHospitalVisitingTime())
                    .build();
        }
    }
}
