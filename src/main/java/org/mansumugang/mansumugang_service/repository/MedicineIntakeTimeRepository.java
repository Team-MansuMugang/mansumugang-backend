package org.mansumugang.mansumugang_service.repository;

import org.mansumugang.mansumugang_service.domain.medicine.Medicine;
import org.mansumugang.mansumugang_service.domain.medicine.MedicineInTakeTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface MedicineIntakeTimeRepository extends JpaRepository<MedicineInTakeTime, Long> {
    void deleteAllByMedicine(Medicine medicine);

    List<MedicineInTakeTime> findAllByMedicine(Medicine foundMedicine);

    Optional<MedicineInTakeTime> findByMedicineAndMedicineIntakeTime(Medicine medicine, LocalTime medicineIntakeTime);
}
