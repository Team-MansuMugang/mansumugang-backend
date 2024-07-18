package org.mansumugang.mansumugang_service.repository;


import org.mansumugang.mansumugang_service.domain.user.Patient;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByUsername(String username);
}
