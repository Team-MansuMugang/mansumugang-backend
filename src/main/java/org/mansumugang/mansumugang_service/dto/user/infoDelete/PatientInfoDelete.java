package org.mansumugang.mansumugang_service.dto.user.infoDelete;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class PatientInfoDelete {


    @Getter
    @AllArgsConstructor
    @Builder
    public static class Dto{
        private String deletedUsername;
        private String deletedName;
        private String usertype;

        public static Dto fromEntity(String username, String name, String usertype ){
            return Dto.builder()
                    .deletedUsername(username)
                    .deletedName(name)
                    .usertype(usertype)
                    .build();
        }

    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response{
        private String message;
        private String deletedUsername;
        private String deletedName;
        private String usertype;

        public static Response createNewResponse(Dto dto){
            return Response.builder()
                    .message("회원 탈퇴가 정상적으로 이루어졌습니다!")
                    .deletedUsername(dto.getDeletedUsername())
                    .deletedName(dto.getDeletedName())
                    .usertype(dto.getUsertype())
                    .build();
        }
    }
}
