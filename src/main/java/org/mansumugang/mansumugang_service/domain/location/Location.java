package org.mansumugang.mansumugang_service.domain.location;

import jakarta.persistence.*;
import lombok.*;
import org.mansumugang.mansumugang_service.domain.user.Patient;
import org.mansumugang.mansumugang_service.dto.location.PatientLocationRequestDto;
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
public class Location {

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

    // Patient 와의 관계 정의
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;



    public Location(double latitude, double longitude, LocalDateTime createdAt, Patient patient) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = createdAt;
        this.patient = patient;
    }

    public static Location fromRequestDto(
            Patient patient,
            PatientLocationRequestDto patientLocationRequestDto
    ){
        return Location.builder()
                .latitude(patientLocationRequestDto.getLatitude())
                .longitude(patientLocationRequestDto.getLongitude())
                .patient(patient)
                .build();
    }

}
