package org.mansumugang.mansumugang_service.repository;

import org.mansumugang.mansumugang_service.domain.medicine.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {
}
