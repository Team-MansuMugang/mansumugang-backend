package org.mansumugang.mansumugang_service.dto.location;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.mansumugang.mansumugang_service.domain.user.Patient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    public static List<PatientLocationResponseDto> fromDtoList(Patient patient, List<PatientLocationDto> patientLocationDtos) {
        return patientLocationDtos.stream()
                .map(dto -> new PatientLocationResponseDto(
                        dto.getName(),
                        dto.getUpdatedTime(),
                        dto.getLongitude(),
                        dto.getLatitude()))
                .collect(Collectors.toList());
    }

}