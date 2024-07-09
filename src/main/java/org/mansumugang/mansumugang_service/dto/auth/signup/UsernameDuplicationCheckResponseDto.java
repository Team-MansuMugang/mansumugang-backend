package org.mansumugang.mansumugang_service.dto.auth.signup;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsernameDuplicationCheckResponseDto {
    String message;

    public UsernameDuplicationCheckResponseDto() {

        this.message = "사용가능한 아이디입니다.";
    }
}
