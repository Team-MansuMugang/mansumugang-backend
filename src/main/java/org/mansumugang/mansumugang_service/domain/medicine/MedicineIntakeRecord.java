package org.mansumugang.mansumugang_service.domain.medicine;

import jakarta.persistence.*;
import lombok.*;
import org.mansumugang.mansumugang_service.constant.MedicineRecordStatusType;
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
public class MedicineIntakeRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Medicine medicine;

    @ManyToOne(fetch = FetchType.LAZY)
    private MedicineIntakeDay medicineIntakeDay;

    @ManyToOne(fetch = FetchType.LAZY)
    private MedicineInTakeTime medicineInTakeTime;

    @Column(nullable = false)
    private LocalDate scheduledIntakeDate;

    @Column(nullable = false)
    private MedicineRecordStatusType status;

    private LocalDateTime actualIntakeTime;

    private Boolean isPushed;

    public static MedicineIntakeRecord createNewEntity(Medicine medicine,
                                                       MedicineIntakeDay medicineIntakeDay,
                                                       MedicineInTakeTime medicineInTakeTime,
                                                       LocalDate scheduledIntakeDate,
                                                       MedicineRecordStatusType status) {
        return MedicineIntakeRecord.builder()
                .medicine(medicine)
                .medicineIntakeDay(medicineIntakeDay)
                .medicineInTakeTime(medicineInTakeTime)
                .scheduledIntakeDate(scheduledIntakeDate)
                .status(status)
                .actualIntakeTime(LocalDateTime.now())
                .isPushed(false)
                .build();
    }


}