package org.mansumugang.mansumugang_service.repository;

import org.mansumugang.mansumugang_service.domain.user.UserLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLocationRepository extends JpaRepository<UserLocation, Long> {
}
