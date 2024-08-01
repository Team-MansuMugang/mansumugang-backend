package org.mansumugang.mansumugang_service.dto.user.location;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.mansumugang.mansumugang_service.domain.user.Patient;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.domain.user.UserLocation;

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
            Patient foundPatient,
            UserLocation userLocation
    ){

        return PatientLocationDto.builder()
                .name(foundPatient.getName())
                .updatedTime(userLocation.getCreatedAt())
                .longitude(userLocation.getLongitude())
                .latitude(userLocation.getLatitude())
                .build();
    }


}