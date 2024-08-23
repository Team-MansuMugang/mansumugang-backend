package org.mansumugang.mansumugang_service.repository;

import org.mansumugang.mansumugang_service.domain.medicine.MedicinePrescription;
import org.mansumugang.mansumugang_service.domain.user.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicinePrescriptionRepository extends JpaRepository<MedicinePrescription, Long> {
    List<MedicinePrescription> findByPatientOrderByCreatedAtDesc(Patient patient);

    List<MedicinePrescription> findAllByPatientId(Long patientId);
}
