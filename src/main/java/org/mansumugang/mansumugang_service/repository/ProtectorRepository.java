package org.mansumugang.mansumugang_service.repository;

import org.mansumugang.mansumugang_service.domain.user.Protector;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProtectorRepository extends JpaRepository<Protector, Long> {
    Optional<Protector> findByUsername(String username);
    Optional<Protector> findByNickname(String nickname);
}
