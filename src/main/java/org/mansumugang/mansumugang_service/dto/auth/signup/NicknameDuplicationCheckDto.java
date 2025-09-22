package org.mansumugang.mansumugang_service.dto.auth.signup;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NicknameDuplicationCheckDto {

    @NotBlank(message = "닉네임은 공백일 수 없습니다.")
    @Size(max = 20, message = "닉네임은 최대 20자리까지 입력해주세요.")
    String nickname;
}
