package org.mansumugang.mansumugang_service.dto.auth.signup;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.mansumugang.mansumugang_service.domain.user.UserType;

@Getter
@Setter
public class PatientSignupRequestDto {

    private String usertype = UserType.USER_PATIENT.toString();

    @NotBlank(message = "아이디는 공백일 수 없습니다.")
    @Size(min = 4, max = 16, message = "아이디는 4 ~ 16자리로 입력해주세요")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "아이디는 영어 소/대문자 및 숫자로 이루어져야합니다.")
    private String username; // 유저 id

    @NotBlank(message = "비밀번호는 공백일 수 없습니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8 ~ 20자리로 입력해주세요")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]+$", message = "비밀번호는 하나 이상의 알파벳, 숫자 및 특수문자로 구성되어야합니다.")
    private String password; // 비밀번호

    @NotBlank(message = "비밀번호는 공백일 수 없습니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8 ~ 20자리로 입력해주세요")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]+$", message = "비밀번호는 하나 이상의 알파벳, 숫자 및 특수문자로 구성되어야합니다.")
    private String passwordCheck; // 비밀번호 확인

    @NotBlank(message = "이름은 공백일 수 없습니다.")
    @Size(min = 2, max = 20, message = "이름이 너무 짧거나 깁니다.")
    private String name; // 이름

    @NotBlank(message = "생년월일은 공백일 수 없습니다.")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "올바른 생년월일 형식은 yyyy-MM-dd 입니다.")
    private String birthdate; // 생년월일

    @NotBlank(message = "전화번호는 공백일 수 없습니다.")
    @Pattern(regexp = "^01[016789]-\\d{3,4}-\\d{4}$", message = "유효한 형식의 휴대폰 번호를 입력해 주세요. 예: 010-1234-5678")
    private String telephone; // 전화번호

    /**
     * 환자와 보호자 사이의 관계를 정립하기 위해 회원가입시에 보호자 아이디 넘겨받음.
     */

    @NotBlank(message = "아이디는 공백일 수 없습니다.")
    @Size(min = 4, max = 16, message = "아이디는 4 ~ 16자리로 입력해주세요")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "아이디는 영어 소/대문자 및 숫자로 이루어져야합니다.")
    private String protectorUsername; // 보호자 아이디



}
