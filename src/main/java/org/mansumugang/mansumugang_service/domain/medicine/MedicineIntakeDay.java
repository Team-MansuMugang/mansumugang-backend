package org.mansumugang.mansumugang_service.domain.medicine;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.mansumugang.mansumugang_service.domain.user.Patient;

import java.time.DayOfWeek;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MedicineIntakeDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Medicine medicine;

    @Column(nullable = false)
    private DayOfWeek day;

    public static MedicineIntakeDay of(Medicine newMedicine, Patient patient,  DayOfWeek dayType) {
        return MedicineIntakeDay.builder()
                .medicine(newMedicine)
                .patient(patient)
                .day(dayType)
                .build();
    }
}

