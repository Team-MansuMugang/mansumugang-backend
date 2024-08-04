package org.mansumugang.mansumugang_service.domain.record;

import jakarta.persistence.*;
import lombok.*;
import org.mansumugang.mansumugang_service.domain.user.Patient;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@Table(name = "userRecord")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Record {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long id;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private Long duration;

    @CreatedDate
    private LocalDateTime createdAt;

    // Patient 와의 관계 정의
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    private Record(String filename, Long duration, LocalDateTime createdAt, Patient patient) {
        this.filename = filename;
        this.duration = duration;
        this.createdAt = createdAt;
        this.patient = patient;
    }

    public static Record of(Patient validPatient,
                            String recordFileName,
                            Long recordDuration
    ){
        return Record.builder()
                .patient(validPatient)
                .filename(recordFileName)
                .duration(recordDuration)
                .build();
    }


}
