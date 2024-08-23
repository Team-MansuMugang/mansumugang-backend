package org.mansumugang.mansumugang_service.domain.user;

import jakarta.persistence.*;
import lombok.*;
import org.mansumugang.mansumugang_service.dto.auth.signup.ProtectorSignUpRequestDto;
import org.mansumugang.mansumugang_service.dto.user.infoUpdate.ProtectorInfoUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;


@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@DiscriminatorValue("Protector")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Protector extends User {

    private String nickname;
    private String email;


    @Builder
    public Protector(
            String username,
            String password,
            String name,
            String birthdate,
            String telephone,
            String usertype,
            String authority,
            String nickname,
            String email
    ) {
        super(username, password, name, birthdate, telephone, usertype, authority);
        this.nickname = nickname;
        this.email = email;
    }

    public static Protector protectorRequestDtoToUser(
            ProtectorSignUpRequestDto protectorSignUpRequestDto,
            PasswordEncoder passwordEncoder
    ) {

        return Protector.builder()
                .username(protectorSignUpRequestDto.getUsername())
                .password(passwordEncoder.encode(protectorSignUpRequestDto.getPassword()))
                .name(protectorSignUpRequestDto.getName())
                .birthdate(protectorSignUpRequestDto.getBirthdate())
                .telephone(protectorSignUpRequestDto.getTelephone())
                .email(protectorSignUpRequestDto.getEmail())
                .nickname(protectorSignUpRequestDto.getNickname())
                .usertype(protectorSignUpRequestDto.getUsertype())
                .authority("ROLE_USER")
                .build();
    }

    public void update(ProtectorInfoUpdate.Request request ,String newNickname){
        super.setName(request.getName());
        super.setBirthdate(request.getBirthdate());
        super.setTelephone(request.getTelephone());
        this.nickname = newNickname;
        this.email = request.getEmail();
        super.setUpdatedAt(LocalDateTime.now());
    }
}
