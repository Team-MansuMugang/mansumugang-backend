package org.mansumugang.mansumugang_service.domain.user;

import jakarta.persistence.*;
import lombok.*;
import org.mansumugang.mansumugang_service.dto.auth.signup.PatientSignupRequestDto;
import org.mansumugang.mansumugang_service.dto.auth.signup.ProtectorSignUpRequestDto;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String username; // 유저 id

    private String password; // 비밀번호

    private String name; // 이름

    private String birthdate; // 사용자 생년월일

    private String telephone; // 사용자 전화번호

    private String nickname; // 사용자 닉네임

    private String email; // 사용자 이메일

    private String usertype; // 환자, 보호자 구분

    private String authority; // 사용자 권한 : User / Admin

    @CreatedDate
    private LocalDateTime createdAt;

    // UserLocation과의 관계 추가
    @OneToMany(mappedBy = "user")
    private List<UserLocation> userLocations;


    public User(
            String username,
            String password,
            String authority,
            String usertype
    ) {
        this.username = username;
        this.password = password;
        this.authority = authority;
        this.usertype = usertype;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton((GrantedAuthority) () -> authority);
    }


    public static User patientRequestDtoToUser(
            PatientSignupRequestDto patientSignupRequestDto,
            PasswordEncoder passwordEncoder
    ) {
        return User.builder()
                .username(patientSignupRequestDto.getUsername())
                .password(passwordEncoder.encode(patientSignupRequestDto.getPassword()))
                .name(patientSignupRequestDto.getName())
                .birthdate(patientSignupRequestDto.getBirthdate())
                .usertype(patientSignupRequestDto.getUsertype())
                .authority("ROLE_USER")
                .build();
    }

    public static User protectorRequestDtoToUser(
            ProtectorSignUpRequestDto protectorSignUpRequestDto, PasswordEncoder passwordEncoder
    ) {

        return User.builder()
                .username(protectorSignUpRequestDto.getUsername())
                .password(passwordEncoder.encode(protectorSignUpRequestDto.getPassword()))
                .name(protectorSignUpRequestDto.getName())
                .birthdate(protectorSignUpRequestDto.getBirthdate())
                .email(protectorSignUpRequestDto.getEmail())
                .nickname(protectorSignUpRequestDto.getNickname())
                .usertype(protectorSignUpRequestDto.getUsertype())
                .authority("ROLE_USER")
                .build();
    }


    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }


}
