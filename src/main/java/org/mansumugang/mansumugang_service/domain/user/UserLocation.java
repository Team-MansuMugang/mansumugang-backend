package org.mansumugang.mansumugang_service.domain.user;

import jakarta.persistence.*;
import lombok.*;
import org.mansumugang.mansumugang_service.dto.user.location.PatientLocationRequestDto;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "userLocation")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class UserLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private double latitude;

    private double longitude;

    @CreatedDate
    private LocalDateTime createdAt;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public UserLocation(Long id, double latitude, double longitude, LocalDateTime createdAt) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = createdAt;
    }

    public static UserLocation fromRequestDto(
            Long userId, PatientLocationRequestDto patientLocationRequestDto
    ){
        return UserLocation.builder()
                .id(userId)
                .latitude(patientLocationRequestDto.getLatitude())
                .longitude(patientLocationRequestDto.getLongitude())
                .build();
    }

}
