package org.mansumugang.mansumugang_service.dto.auth.signup;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProtectorUsernameCheckResponseDto {
    String message;

    public ProtectorUsernameCheckResponseDto() {
        this.message = "해당 아이디는 보호자가 맞습니다.";
    }
}
