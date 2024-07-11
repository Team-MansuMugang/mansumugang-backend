package org.mansumugang.mansumugang_service.dto.user.location;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class PatientLocationResponseDto {

    private Long id;
    private double longitude;
    private double latitude;

    public static PatientLocationResponseDto DtoToResponse(
            PatientLocationDto patientLocationDto
    ){

        return PatientLocationResponseDto.builder()
                .id(patientLocationDto.getId())
                .longitude(patientLocationDto.getLongitude())
                .latitude(patientLocationDto.getLatitude())
                .build();
    }

}
