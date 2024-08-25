package org.mansumugang.mansumugang_service.domain.medicine;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.mansumugang.mansumugang_service.domain.user.Patient;
import org.mansumugang.mansumugang_service.dto.medicine.MedicinePrescriptionSave;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class MedicinePrescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Patient patient;

    private String medicinePrescriptionImageName;

    @CreatedDate
    private LocalDateTime createdAt;

    public static MedicinePrescription of(String medicinePrescriptionImageName, Patient patient) {
        return MedicinePrescription.builder()
                .patient(patient)
                .medicinePrescriptionImageName(medicinePrescriptionImageName)
                .build();
    }
}
