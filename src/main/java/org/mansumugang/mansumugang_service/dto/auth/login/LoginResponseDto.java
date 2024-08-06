package org.mansumugang.mansumugang_service.dto.auth.login;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class LoginResponseDto {


    private String accessToken;
    private String refreshToken;
    private String userType;

    public static LoginResponseDto of(
            String accessToken,
            String refreshToken,
            String userType
    ){
        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userType(userType)
                .build();
    }
}
