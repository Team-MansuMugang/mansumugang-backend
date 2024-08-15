package org.mansumugang.mansumugang_service.dto.auth.signup;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProtectorUsernameCheckRequestDto {

    @NotBlank(message = "아이디는 공백일 수 없습니다.")
    @Size(min = 4, max = 16, message = "아이디는 4 ~ 16자리로 입력해주세요")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "아이디는 영어 소/대문자 및 숫자로 이루어져야합니다.")
    String username;
}
