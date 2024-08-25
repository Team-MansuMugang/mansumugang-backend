package org.mansumugang.mansumugang_service.dto.user.familyMember;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.mansumugang.mansumugang_service.domain.user.Patient;
import org.mansumugang.mansumugang_service.domain.user.Protector;


import java.util.List;
import java.util.stream.Collectors;

public class FamilyMemberInquiry {

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Dto{
        private String imageApiUrl;
        private FamilyMember.Self self;
        private FamilyMember.Nok protector;
        private List<FamilyMember.OtherPatient> otherPatients;

        public static Dto of(Patient validPatient, Protector foundProtector, List<Patient> otherPatients, String imageApiUrl){
            return Dto.builder()
                    .imageApiUrl(imageApiUrl)
                    .self(FamilyMember.Self.fromEntity(validPatient))
                    .protector(FamilyMember.Nok.fromEntity(foundProtector))
                    .otherPatients((otherPatients.stream()
                            .map(FamilyMember.OtherPatient::fromEntity)
                            .collect(Collectors.toList())))
                    .build();

        }
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response{
        private String imageApiUrl;
        private FamilyMember.Self self;
        private FamilyMember.Nok protector;
        private List<FamilyMember.OtherPatient> otherPatients;

        public static Response createNewResponse(Dto dto){
            return Response.builder()
                    .imageApiUrl(dto.imageApiUrl)
                    .self(dto.getSelf())
                    .protector(dto.getProtector())
                    .otherPatients(dto.getOtherPatients())
                    .build();
        }
    }
}
