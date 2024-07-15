package org.mansumugang.mansumugang_service.dto.user.location;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientLocationRequestDto {

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

}
