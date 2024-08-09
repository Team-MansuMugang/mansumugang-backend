package org.mansumugang.mansumugang_service.domain.hospital;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.mansumugang.mansumugang_service.domain.user.Patient;
import org.mansumugang.mansumugang_service.dto.hospital.HospitalSave;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Hospital {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Patient patient;

    @Column(nullable = false)
    private String hospitalName;

    @Column(nullable = false)
    private String hospitalAddress;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Lob
    private String hospitalDescription;

    @Column(nullable = false)
    private LocalDateTime hospitalVisitingTime;

    @ColumnDefault("false")
    @Builder.Default()
    private Boolean status = false;

    private LocalDateTime actualHospitalVisitingTime;


    public static Hospital of(HospitalSave.Request requestDto, Patient patient, LocalDateTime filteredLocalDateTime){
        return Hospital.builder()
                .patient(patient)
                .hospitalName(requestDto.getHospitalName())
                .hospitalAddress(requestDto.getHospitalAddress())
                .latitude(requestDto.getLatitude())
                .longitude(requestDto.getLongitude())
                .hospitalDescription(requestDto.getHospitalDescription())
                .hospitalVisitingTime(filteredLocalDateTime)
                .build();
    }
}
