package org.mansumugang.mansumugang_service.dto.auth.login;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginRequestDto {

    @NotNull(message = "아이디를 입력해주세요!")
    private String username;

    @NotNull(message = "비밀번호를 입력해주세요!")
    private String password;


}
