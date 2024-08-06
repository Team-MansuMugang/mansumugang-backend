package org.mansumugang.mansumugang_service.dto.user;

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

        public static PatientElement fromEntity(Patient patient){
            return PatientElement.builder()
                    .patientId(patient.getId())
                    .patientUsername(patient.getUsername())
                    .patientName(patient.getName())
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Dto{

        private List<PatientElement> patients;

        public static Dto fromEntity(List<Patient> foundPatients){
            return Dto.builder()
                    .patients(foundPatients.stream()
                            .map(patient -> PatientElement.fromEntity(patient))
                            .collect(Collectors.toList()))
                    .build();
        }

    }

    @Getter
    @Builder
    public static class Response{

        private List<PatientElement> patients;

        public static Response createNewResponse(Dto dto){
            return Response.builder()
                    .patients(dto.getPatients())
                    .build();
        }
    }
}
