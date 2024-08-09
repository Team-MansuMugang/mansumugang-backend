package org.mansumugang.mansumugang_service.repository;

import org.mansumugang.mansumugang_service.domain.hospital.Hospital;
import org.mansumugang.mansumugang_service.domain.user.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HospitalRepository extends JpaRepository<Hospital, Long> {
    List<Hospital> findAllByPatientIdAndHospitalVisitingTimeBetween(Long patientId, LocalDateTime hospitalVisitingTime, LocalDateTime hospitalVisitingTime2);

    Optional<Hospital> findByPatientAndHospitalVisitingTime(Patient patient, LocalDateTime hospitalVisitingTime);
}
