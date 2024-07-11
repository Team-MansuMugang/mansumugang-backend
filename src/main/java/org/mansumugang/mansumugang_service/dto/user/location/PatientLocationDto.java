package org.mansumugang.mansumugang_service.dto.user.location;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.mansumugang.mansumugang_service.domain.user.UserLocation;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class PatientLocationDto {

    private Long id;
    private double longitude;
    private double latitude;

    public static PatientLocationDto fromEntity(
            UserLocation userLocation
    ){

        return PatientLocationDto.builder()
                .id(userLocation.getId())
                .longitude(userLocation.getLongitude())
                .latitude(userLocation.getLatitude())
                .build();
    }

}
