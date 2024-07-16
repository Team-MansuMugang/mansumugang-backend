package org.mansumugang.mansumugang_service.dto.user.location;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class PatientLocationResponseDto {

    private String name;
    private LocalDateTime updatedTime;
    private double longitude;
    private double latitude;

    public static PatientLocationResponseDto DtoToResponse(
            PatientLocationDto patientLocationDto
    ){

        return PatientLocationResponseDto.builder()
                .name(patientLocationDto.getName())
                .updatedTime(patientLocationDto.getUpdatedTime())
                .longitude(patientLocationDto.getLongitude())
                .latitude(patientLocationDto.getLatitude())
                .build();
    }

}