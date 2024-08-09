package org.mansumugang.mansumugang_service.dto.auth.logout;


import lombok.Data;

@Data
public class LogoutResponseDto {
    private String message;

    public LogoutResponseDto(){
        this.message = "로그아웃 되었습니다.";
    }

}
