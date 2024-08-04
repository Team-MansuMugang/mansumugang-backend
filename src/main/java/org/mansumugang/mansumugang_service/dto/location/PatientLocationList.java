package org.mansumugang.mansumugang_service.dto.location;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.mansumugang.mansumugang_service.domain.location.Location;
import org.mansumugang.mansumugang_service.domain.user.Patient;

import java.time.LocalDateTime;
import java.util.List;


public class PatientLocationList {
    @Getter
    @AllArgsConstructor
    @Builder
    public static class LocationElement{
        private LocalDateTime createdTime;
        private double longitude;
        private double latitude;

        public static LocationElement fromEntity(Location location){
            return LocationElement.builder()
                    .createdTime(location.getCreatedAt())
                    .longitude(location.getLongitude())
                    .latitude(location.getLatitude())
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Dto{
        private String name;
        private List<LocationElement> locations;

        public static Dto of(Patient patient, List<LocationElement> location){
            return Dto.builder()
                    .name(patient.getName())
                    .locations(location)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response{
        private String name;
        private List<LocationElement> locations;

        public static Response fromDto(Dto dto){
            return Response.builder()
                    .name(dto.getName())
                    .locations(dto.getLocations())
                    .build();
        }
    }

}