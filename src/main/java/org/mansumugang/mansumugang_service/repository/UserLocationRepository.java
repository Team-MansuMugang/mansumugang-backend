package org.mansumugang.mansumugang_service.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.mansumugang.mansumugang_service.domain.user.Patient;
import org.mansumugang.mansumugang_service.domain.location.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserLocationRepository extends JpaRepository<Location, Long> {

    Optional<Location> findTopByPatientOrderByCreatedAtDesc(@Param("patient")Patient patient);

    @Query("SELECT ul FROM Location ul WHERE ul.patient = :patient AND ul.createdAt BETWEEN :startTime AND :endTime ORDER BY ul.createdAt DESC")
    List<Location> findByPatientWithinTimeRange(@Param("patient") Patient patient, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}