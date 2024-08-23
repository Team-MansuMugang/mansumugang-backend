package org.mansumugang.mansumugang_service.dto.user.inquiry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.mansumugang.mansumugang_service.domain.user.Protector;

import java.time.LocalDateTime;

public class ProtectorInfoInquiry {

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Dto{
        private Long protectorId;
        private String username;
        private String name; // 유저 실명 추가
        private String email;
        private String nickname;
        private String birthdate;
        private String telephone; // 전화번호 추가
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt; // 수정시간 추가
        private String usertype;

        public static Dto fromEntity(Protector foundProtector){
            return Dto.builder()
                    .protectorId(foundProtector.getId())
                    .username(foundProtector.getUsername())
                    .name(foundProtector.getName())
                    .email(foundProtector.getEmail())
                    .nickname(foundProtector.getNickname())
                    .birthdate(foundProtector.getBirthdate())
                    .telephone(foundProtector.getTelephone())
                    .createdAt(foundProtector.getCreatedAt())
                    .updatedAt(foundProtector.getUpdatedAt())
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
        private String name; // 유저 실명 추가
        private String email;
        private String nickname;
        private String birthdate;
        private String telephone; // 전화번호 추가.
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String usertype;

        public static Response createNewResponse(Dto dto){
            return Response.builder()
                    .message("유저 정보를 정상적으로 불러왔습니다.")
                    .protectorId(dto.getProtectorId())
                    .username(dto.getUsername())
                    .name(dto.getName())
                    .email(dto.getEmail())
                    .nickname(dto.getNickname())
                    .birthdate(dto.getBirthdate())
                    .telephone(dto.getTelephone())
                    .createdAt(dto.getCreatedAt())
                    .updatedAt(dto.getUpdatedAt())
                    .usertype(dto.getUsertype())
                    .build();
        }
    }
}
