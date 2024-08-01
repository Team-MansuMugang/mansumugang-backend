package org.mansumugang.mansumugang_service.domain.user;


import jakarta.persistence.*;
import lombok.*;
import org.mansumugang.mansumugang_service.dto.auth.signup.PatientSignupRequestDto;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Entity
@DiscriminatorValue("Patient")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Patient extends User {

    // Protector 와의 관계 정의
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "protector_id", nullable = false)
    private Protector protector;

    // UserLocation 과의 관계 추가
    @OneToMany(mappedBy = "patient")
    private List<UserLocation> userLocations;

    @Builder
    public Patient(
            String username,
            String password,
            String name,
            String birthdate,
            String usertype,
            String authority,
            Protector protector
    ) {
        super(username, password, name, birthdate, usertype, authority);
        this.protector = protector;
    }

    public static Patient patientRequestDtoToUser(
            Protector foundProtector,
            PatientSignupRequestDto patientSignupRequestDto,
            PasswordEncoder passwordEncoder
    ) {
        return Patient.builder()
                .username(patientSignupRequestDto.getUsername())
                .password(passwordEncoder.encode(patientSignupRequestDto.getPassword()))
                .name(patientSignupRequestDto.getName())
                .birthdate(patientSignupRequestDto.getBirthdate())
                .usertype(patientSignupRequestDto.getUsertype())
                .authority("ROLE_USER")
                .protector(foundProtector)
                .build();
    }

}
