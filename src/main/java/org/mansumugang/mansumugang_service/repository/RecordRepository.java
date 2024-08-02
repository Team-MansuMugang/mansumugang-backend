package org.mansumugang.mansumugang_service.repository;

import org.mansumugang.mansumugang_service.domain.record.Record;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecordRepository extends JpaRepository<Record, Long> {
    List<Record> findByPatient_idOrderByCreatedAtDesc(long patient_id);
}
