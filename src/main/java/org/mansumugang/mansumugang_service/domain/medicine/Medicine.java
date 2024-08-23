package org.mansumugang.mansumugang_service.domain.medicine;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.mansumugang.mansumugang_service.domain.user.Patient;
import org.mansumugang.mansumugang_service.dto.medicine.MedicineSave;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Medicine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Patient patient;

    @Column(nullable = false)
    private String medicineName;

    private String hospitalName;

    private String medicineImageName;

    private String medicineDescription;

    private LocalDate intakeStopDate;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public static Medicine of(Patient patient,
                              MedicineSave.Request requestDto,
                              LocalDate parsedMedicineIntakeStopDay,
                              String medicineImageName) {
        return Medicine.builder()
                .patient(patient)
                .medicineName(requestDto.getMedicineName())
                .hospitalName(requestDto.getHospitalName())
                .medicineImageName(medicineImageName)
                .medicineDescription(requestDto.getMedicineDescription())
                .intakeStopDate(parsedMedicineIntakeStopDay)
                .build();
    }
}