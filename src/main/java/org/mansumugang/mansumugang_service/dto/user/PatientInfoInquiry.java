package org.mansumugang.mansumugang_service.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.mansumugang.mansumugang_service.domain.user.Patient;

import java.time.LocalDateTime;

public class PatientInfoInquiry {

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Dto{
        private Long patientId;
        private String username;
        private String name; // 환자 이름 추가
        private String birthdate;
        private String telephone; // 전화번호 추가
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt; // 수정시간 추가
        private String usertype;

        public static Dto fromEntity(Patient foundPatient){
            return Dto.builder()
                    .patientId(foundPatient.getId())
                    .username(foundPatient.getUsername())
                    .name(foundPatient.getName())
                    .birthdate(foundPatient.getBirthdate())
                    .telephone(foundPatient.getTelephone())
                    .createdAt(foundPatient.getCreatedAt())
                    .updatedAt(foundPatient.getUpdatedAt())
                    .usertype(foundPatient.getUsertype())
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response{
        private String message;
        private Long patientId;
        private String username;
        private String name; // 환자 이름 추가
        private String birthdate;
        private String telephone; // 전화번호 추가.
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String usertype;

        public static Response createNewResponse(Dto dto){
            return Response.builder()
                    .message("유저 정보를 정상적으로 불러왔습니다.")
                    .patientId(dto.getPatientId())
                    .username(dto.getUsername())
                    .name(dto.getName())
                    .birthdate(dto.getBirthdate())
                    .telephone(dto.getTelephone())
                    .createdAt(dto.getCreatedAt())
                    .updatedAt(dto.getUpdatedAt())
                    .usertype(dto.getUsertype())
                    .build();
        }
    }
}
