package org.mansumugang.mansumugang_service.repository;

import org.mansumugang.mansumugang_service.domain.medicine.Medicine;
import org.mansumugang.mansumugang_service.domain.medicine.MedicineInTakeTime;
import org.mansumugang.mansumugang_service.domain.medicine.MedicineIntakeDay;
import org.mansumugang.mansumugang_service.domain.medicine.MedicineIntakeRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface MedicineIntakeRecordRepository extends JpaRepository<MedicineIntakeRecord, Long> {
    void deleteAllByMedicine(Medicine foundMedicine);

    void deleteByMedicineIntakeDay(MedicineIntakeDay foundMedicineIntakeDay);

    void deleteByMedicineInTakeTime(MedicineInTakeTime medicineInTakeTime);

    Optional<MedicineIntakeRecord> findByMedicineAndMedicineIntakeDayAndMedicineInTakeTimeAndScheduledIntakeDate(
            Medicine medicine,
            MedicineIntakeDay medicineIntakeDay,
            MedicineInTakeTime medicineInTakeTime,
            LocalDate scheduledIntakeDate);
}
