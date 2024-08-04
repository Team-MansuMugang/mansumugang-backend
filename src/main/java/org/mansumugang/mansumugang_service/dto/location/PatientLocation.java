package org.mansumugang.mansumugang_service.dto.location;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.mansumugang.mansumugang_service.domain.location.Location;
import org.mansumugang.mansumugang_service.domain.user.Patient;

import java.time.LocalDateTime;

public class PatientLocation {

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Dto{
        private String name;
        private LocalDateTime createdTime;
        private double longitude;
        private double latitude;

        public static PatientLocation.Dto of(Patient patient, Location location){
            return PatientLocation.Dto.builder()
                    .name(patient.getName())
                    .createdTime(location.getCreatedAt())
                    .longitude(location.getLongitude())
                    .latitude(location.getLatitude())
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response{
        private String name;
        private LocalDateTime createdTime;
        private double longitude;
        private double latitude;


        public static PatientLocation.Response fromDto(PatientLocation.Dto dto){
            return Response.builder()
                    .name(dto.getName())
                    .latitude(dto.getLatitude())
                    .longitude(dto.longitude)
                    .createdTime(dto.createdTime)
                    .build();
        }
    }

}
