package org.mansumugang.mansumugang_service.repository;

import org.mansumugang.mansumugang_service.domain.record.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RecordRepository extends JpaRepository<Record, Long> {
    List<Record> findByPatientIdOrderByCreatedAtDesc(long patient_id);

    @Query("SELECT r FROM Record r WHERE r.patient.id IN :patientIds ORDER BY r.createdAt DESC")
    List<Record> findAllByPatientIdsOrderByCreatedAtDesc(@Param("patientIds") List<Long> patientIds);

    List<Record> findAllByPatientId(long patient_id);

    int countByPatientIdAndCreatedAtBetween(Long patientId, LocalDateTime start, LocalDateTime end);

}
