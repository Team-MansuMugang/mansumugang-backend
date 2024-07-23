package org.mansumugang.mansumugang_service.repository;

import org.mansumugang.mansumugang_service.domain.medicine.Medicine;
import org.mansumugang.mansumugang_service.domain.medicine.MedicineIntakeDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

public interface MedicineIntakeDayRepository extends JpaRepository<MedicineIntakeDay, Long> {
    void deleteAllByMedicine(Medicine foundMedicine);

    List<MedicineIntakeDay> findAllByMedicine(Medicine Medicine);

    Optional<MedicineIntakeDay> findByMedicineAndDay(Medicine medicine, DayOfWeek day);
}
