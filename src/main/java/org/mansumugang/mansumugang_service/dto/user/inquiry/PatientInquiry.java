package org.mansumugang.mansumugang_service.dto.user.inquiry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.mansumugang.mansumugang_service.domain.user.Patient;

import java.util.List;
import java.util.stream.Collectors;

public class PatientInquiry {

    @Getter
    @AllArgsConstructor
    @Builder
    public static class PatientElement{

        private Long patientId;
        private String patientUsername;
        private String patientName;
        private String patientProfileImageName;

        public static PatientElement fromEntity(Patient patient){
            return PatientElement.builder()
                    .patientId(patient.getId())
                    .patientUsername(patient.getUsername())
                    .patientName(patient.getName())
                    .patientProfileImageName(patient.getProfileImageName())
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Dto{

        private String imageApiUrl;
        private List<PatientElement> patients;

        public static Dto fromEntity(List<Patient> foundPatients, String imageApiUrl){
            return Dto.builder()
                    .imageApiUrl(imageApiUrl)
                    .patients(foundPatients.stream()
                            .map(patient -> PatientElement.fromEntity(patient))
                            .collect(Collectors.toList()))
                    .build();
        }

    }

    @Getter
    @Builder
    public static class Response{

        private String imageApiUrl;
        private List<PatientElement> patients;

        public static Response createNewResponse(Dto dto){
            return Response.builder()
                    .imageApiUrl(dto.getImageApiUrl())
                    .patients(dto.getPatients())
                    .build();
        }
    }
}
