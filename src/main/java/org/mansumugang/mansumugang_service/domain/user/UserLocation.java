package org.mansumugang.mansumugang_service.domain.user;

import jakarta.persistence.*;
import lombok.*;
import org.mansumugang.mansumugang_service.dto.user.location.PatientLocationRequestDto;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "userLocation")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class UserLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    private Long id;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @CreatedDate
    private LocalDateTime createdAt;

    // User와의 관계 정의
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;



    public UserLocation(double latitude, double longitude, LocalDateTime createdAt, User user) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = createdAt;
        this.user = user;
    }

    public static UserLocation fromRequestDto(
            User user, PatientLocationRequestDto patientLocationRequestDto
    ){
        return UserLocation.builder()
                .latitude(patientLocationRequestDto.getLatitude())
                .longitude(patientLocationRequestDto.getLongitude())
                .user(user)
                .build();
    }

}
