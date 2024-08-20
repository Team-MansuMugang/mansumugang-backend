package org.mansumugang.mansumugang_service.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.mansumugang.mansumugang_service.domain.user.Protector;

import java.time.LocalDateTime;

public class ProtectorInquiry {

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Dto{
        private Long protectorId;
        private String username;
        private String email;
        private String nickname;
        private String birthdate;
        private LocalDateTime createdAt;
        private String usertype;

        public static Dto fromEntity(Protector foundProtector){
            return Dto.builder()
                    .protectorId(foundProtector.getId())
                    .username(foundProtector.getUsername())
                    .email(foundProtector.getEmail())
                    .nickname(foundProtector.getNickname())
                    .birthdate(foundProtector.getBirthdate())
                    .createdAt(foundProtector.getCreatedAt())
                    .usertype(foundProtector.getUsertype())
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response{
        private String message;
        private Long protectorId;
        private String username;
        private String email;
        private String nickname;
        private String birthdate;
        private LocalDateTime createdAt;
        private String usertype;

        public static Response createNewResponse(Dto dto){
            return Response.builder()
                    .message("유저 정보를 정상적으로 불러왔습니다.")
                    .protectorId(dto.getProtectorId())
                    .username(dto.getUsername())
                    .email(dto.getEmail())
                    .nickname(dto.getNickname())
                    .birthdate(dto.getBirthdate())
                    .createdAt(dto.getCreatedAt())
                    .usertype(dto.getUsertype())
                    .build();
        }
    }
}
