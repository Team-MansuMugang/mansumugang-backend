package org.mansumugang.mansumugang_service.domain.medicine;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MedicineInTakeTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Medicine medicine;

    @Column(nullable = false)
    private LocalTime medicineIntakeTime;

    public static MedicineInTakeTime of(Medicine medicine, LocalTime intakeTime) {
        return MedicineInTakeTime.builder()
                .medicine(medicine)
                .medicineIntakeTime(
                        LocalTime.of(intakeTime.getHour(), intakeTime.getMinute())
                )
                .build();
    }
}

