package org.mansumugang.mansumugang_service.repository;

import org.mansumugang.mansumugang_service.domain.hospital.Hospital;
import org.mansumugang.mansumugang_service.domain.user.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HospitalRepository extends JpaRepository<Hospital, Long> {
    List<Hospital> findAllByPatientIdAndHospitalVisitingTimeBetween(Long patientId, LocalDateTime hospitalVisitingTime, LocalDateTime hospitalVisitingTime2);

    Optional<Hospital> findByPatientAndHospitalVisitingTime(Patient patient, LocalDateTime hospitalVisitingTime);

//    @Query("SELECT h FROM Hospital h WHERE " +
//            "h.status = false AND " +
//            "h.isPushed = false AND " +
//            ":overdueTime >= h.hospitalVisitingTime ")
//    List<Hospital> findHospitalsWithConditions(@Param("overdueTime") LocalDateTime overdueTime);

    List<Hospital> findByStatusAndIsPushedAndHospitalVisitingTimeBefore(Boolean status, Boolean isPushed, LocalDateTime graceTime);

}
