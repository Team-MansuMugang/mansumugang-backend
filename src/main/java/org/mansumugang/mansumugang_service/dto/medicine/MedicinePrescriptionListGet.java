package org.mansumugang.mansumugang_service.dto.medicine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.mansumugang.mansumugang_service.domain.medicine.MedicinePrescription;
import org.mansumugang.mansumugang_service.domain.user.Patient;

import java.time.LocalDateTime;
import java.util.List;

public class MedicinePrescriptionListGet {
    @Getter
    @AllArgsConstructor
    @Builder
    public static class MedicinePrescriptionListGetElement {
        private Long id;

        private String medicinePrescriptionImageName;

        private LocalDateTime createdAt;

        public static MedicinePrescriptionListGetElement of(MedicinePrescription medicinePrescription){
            return MedicinePrescriptionListGetElement.builder()
                    .id(medicinePrescription.getId())
                    .medicinePrescriptionImageName(medicinePrescription.getMedicinePrescriptionImageName())
                    .createdAt(medicinePrescription.getCreatedAt())
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Dto {
        private Long patientId;

        private String imageApiUrlPrefix;

        private List<MedicinePrescriptionListGetElement> medicinePrescriptions;

        public static Dto fromEntity(Long patientId, List<MedicinePrescription> medicinePrescriptionEntity, String imageApiUrlPrefix){
            return Dto.builder()
                    .imageApiUrlPrefix(imageApiUrlPrefix)
                    .patientId(patientId)
                    .medicinePrescriptions(medicinePrescriptionEntity.stream().map(MedicinePrescriptionListGetElement::of).toList())
                    .build();
        }

    }


    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long patientId;

        private String imageApiUrlPrefix;

        private List<MedicinePrescriptionListGetElement> medicinePrescriptions;

        public static Response fromDto(Dto dto){
            return Response.builder()
                    .patientId(dto.getPatientId())
                    .imageApiUrlPrefix(dto.getImageApiUrlPrefix())
                    .medicinePrescriptions(dto.getMedicinePrescriptions())
                    .build();
        }
    }

}
