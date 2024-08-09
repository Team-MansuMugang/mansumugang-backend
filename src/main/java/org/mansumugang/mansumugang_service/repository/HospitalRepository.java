package org.mansumugang.mansumugang_service.repository;

import org.mansumugang.mansumugang_service.domain.hospital.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HospitalRepository extends JpaRepository<Hospital, Long> {
}
