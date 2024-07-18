package org.mansumugang.mansumugang_service.dto.auth.signup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.mansumugang.mansumugang_service.domain.user.Patient;
import org.mansumugang.mansumugang_service.domain.user.Protector;
import org.mansumugang.mansumugang_service.domain.user.User;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class SignUpDto {

    private String username;
    private String authority;
    private String usertype;

    public static SignUpDto fromPatientEntity(Patient patient){
        return SignUpDto.builder()
                .username(patient.getUsername())
                .usertype(patient.getUsertype())
                .authority(patient.getAuthority())
                .build();
    }

    public static SignUpDto fromProtectorEntity(Protector protector){
        return SignUpDto.builder()
                .username(protector.getUsername())
                .usertype(protector.getUsertype())
                .authority(protector.getAuthority())
                .build();
    }

}
