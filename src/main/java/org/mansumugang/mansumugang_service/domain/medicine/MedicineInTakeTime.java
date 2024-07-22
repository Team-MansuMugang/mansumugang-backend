package org.mansumugang.mansumugang_service.domain.medicine;

import jakarta.persistence.*;
import lombok.*;
import org.mansumugang.mansumugang_service.constant.DayType;
import org.mansumugang.mansumugang_service.dto.medicine.common.MedicineIntakeTimeDto;

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
    private Medicine medicine;

    @Column(nullable = false)
    private LocalTime medicineIntakeTime;

    public static MedicineInTakeTime of(Medicine medicine, MedicineIntakeTimeDto medicineIntakeTimeDto) {
        return MedicineInTakeTime.builder()
                .medicine(medicine)
                .medicineIntakeTime(
                        LocalTime.of(medicineIntakeTimeDto.getMedicineIntakeHours(), medicineIntakeTimeDto.getMedicineIntakeMinutes())
                )
                .build();
    }
}

