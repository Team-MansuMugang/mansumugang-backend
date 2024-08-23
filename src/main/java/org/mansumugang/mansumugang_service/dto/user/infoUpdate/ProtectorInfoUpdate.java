package org.mansumugang.mansumugang_service.dto.user.infoUpdate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.mansumugang.mansumugang_service.domain.user.Protector;

import java.time.LocalDateTime;

public class ProtectorInfoUpdate {

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class Request{

        @NotBlank(message = "이름은 공백일 수 없습니다.")
        @Size(min = 2, max = 20, message = "이름이 너무 짧거나 깁니다.")
        private String name; // 이름

        @NotBlank(message = "생년월일은 공백일 수 없습니다.")
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "올바른 생년월일 형식은 yyyy-MM-dd 입니다.")
        private String birthdate; // 생년월일

        @NotBlank(message = "전화번호는 공백일 수 없습니다.")
        @Pattern(regexp = "^01[016789]-\\d{3,4}-\\d{4}$", message = "유효한 형식의 휴대폰 번호를 입력해 주세요. 예: 010-1234-5678")
        private String telephone; // 전화번호

        @Email(message = "유효하지 않은 이메일입니다.")
        @NotBlank(message = "이메일은 공백일 수 없습니다.")
        @Size(max = 30, message = "이메일은 최대 30자리까지 입력 가능합니다.")
        private String email; // 사용자 이메일

        @NotBlank(message = "닉네임은 공백일 수 없습니다.")
        @Size(max = 20, message = "닉네임은 최대 20자리까지 입력해주세요.")
        private String nickname; // 사용자 닉네임

    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Dto{
        private String updatedName;
        private String updatedBirthdate;
        private String updatedTelephone;
        private String updatedEmail;
        private String updatedNickname;
        private LocalDateTime updatedAt;

        public static Dto fromEntity(Protector validProtector){
            return Dto.builder()
                    .updatedName(validProtector.getName())
                    .updatedBirthdate(validProtector.getBirthdate())
                    .updatedTelephone(validProtector.getTelephone())
                    .updatedEmail(validProtector.getEmail())
                    .updatedNickname(validProtector.getNickname())
                    .updatedAt(validProtector.getUpdatedAt())
                    .build();
        }

    }


    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response{
        private String message;
        private String updatedBirthdate;
        private String updatedTelephone;
        private String updatedEmail;
        private String updatedNickname;
        private LocalDateTime updatedAt;

        public static Response createNewResponse(Dto dto){
            return Response.builder()
                    .message("회원님의 정보가 정상적으로 수정되었습니다!")
                    .updatedBirthdate(dto.getUpdatedBirthdate())
                    .updatedTelephone(dto.getUpdatedTelephone())
                    .updatedEmail(dto.getUpdatedEmail())
                    .updatedNickname(dto.getUpdatedNickname())
                    .updatedAt(dto.getUpdatedAt())
                    .build();
        }

    }
}
