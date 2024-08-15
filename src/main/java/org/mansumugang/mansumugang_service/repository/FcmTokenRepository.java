package org.mansumugang.mansumugang_service.repository;

import org.mansumugang.mansumugang_service.domain.fcm.FcmToken;
import org.mansumugang.mansumugang_service.domain.user.Protector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE FcmToken f SET f.fcmToken = :fcmToken WHERE f.protector = :protector")
    void updateFcmToken(Protector protector, String fcmToken);


    List<FcmToken> findByProtectorId(Long userId);

}
