package org.mansumugang.mansumugang_service.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.domain.user.UserLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserLocationRepository extends JpaRepository<UserLocation, Long> {

    Optional<UserLocation> findTopByUserOrderByCreatedAtDesc(@Param("user") User user);
}