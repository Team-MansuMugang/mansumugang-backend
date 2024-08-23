package org.mansumugang.mansumugang_service.domain.fcm;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.mansumugang.mansumugang_service.domain.user.Protector;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@Table(name = "userFcmToken")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@EntityListeners(AuditingEntityListener.class)
public class FcmToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long id;

    @Column(nullable = false)
    private String fcmToken;

    @CreatedDate
    private LocalDateTime createdAt;


    // Protector 와의 관계 정의
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "protector_id", nullable = false)
    private Protector protector;

    private FcmToken(Long id, String fcmToken, LocalDateTime createdAt, Protector protector) {
        this.id = id;
        this.fcmToken = fcmToken;
        this.createdAt = createdAt;
        this.protector = protector;
    }

    public static FcmToken of(String validFcmToken,
                              Protector validProtector
    ){
        return FcmToken.builder()
                .fcmToken(validFcmToken)
                .protector(validProtector)
                .build();
    }
}
