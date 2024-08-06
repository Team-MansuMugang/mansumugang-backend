package org.mansumugang.mansumugang_service.dto.auth.signup;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NicknameDuplicationCheckResponseDto {

    String message;

    public NicknameDuplicationCheckResponseDto() {
        this.message = "사용 가능한 닉네임 입니다.";
    }
}
