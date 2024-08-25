package org.mansumugang.mansumugang_service.dto.user.familyMember;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.mansumugang.mansumugang_service.domain.user.Patient;
import org.mansumugang.mansumugang_service.domain.user.Protector;


public class FamilyMember {

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Self{

        private String name;
        private String telephone;
        private String usertype;
        private String profileImageName;

        public static Self fromEntity(Patient validPatient){
            return Self.builder()
                    .name(validPatient.getName())
                    .telephone(validPatient.getTelephone())
                    .usertype(validPatient.getUsertype())
                    .profileImageName(validPatient.getProfileImageName())
                    .build();
        }
    }


    @Getter
    @AllArgsConstructor
    @Builder
    public static class Nok{

        private String name;
        private String telephone;
        private String usertype;
        private String profileImageName;

        public static Nok fromEntity(Protector foundProtector){
            return Nok.builder()
                    .name(foundProtector.getName())
                    .telephone(foundProtector.getTelephone())
                    .usertype(foundProtector.getUsertype())
                    .profileImageName(foundProtector.getProfileImageName())
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class OtherPatient{

        private String name;
        private String telephone;
        private String usertype;
        private String profileImageName;

        public static OtherPatient fromEntity(Patient otherPatient){
            return OtherPatient.builder()
                    .name(otherPatient.getName())
                    .telephone(otherPatient.getTelephone())
                    .usertype(otherPatient.getUsertype())
                    .profileImageName(otherPatient.getProfileImageName())
                    .build();
        }
    }

}
