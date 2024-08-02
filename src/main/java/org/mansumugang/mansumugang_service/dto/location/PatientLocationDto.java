package org.mansumugang.mansumugang_service.dto.location;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.mansumugang.mansumugang_service.domain.location.Location;
import org.mansumugang.mansumugang_service.domain.user.Patient;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class PatientLocationDto {

    private String name;
    private LocalDateTime updatedTime;
    private double longitude;
    private double latitude;


    public static PatientLocationDto fromEntity(
            Patient patient,
            Location location
    ){

        return PatientLocationDto.builder()
                .name(patient.getName())
                .updatedTime(location.getCreatedAt())
                .longitude(location.getLongitude())
                .latitude(location.getLatitude())
                .build();
    }


}