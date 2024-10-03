package org.mansumugang.mansumugang_service.domain.record;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
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

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @CreatedDate
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    private Record(String filename, Long duration, String content ,LocalDateTime createdAt, Patient patient) {
        this.filename = filename;
        this.duration = duration;
        this.content = content;
        this.createdAt = createdAt;
        this.patient = patient;
    }

    public static Record of(Patient validPatient,
                            String recordFileName,
                            String transcriptionText,
                            Long recordDuration
    ){
        return Record.builder()
                .patient(validPatient)
                .filename(recordFileName)
                .content(transcriptionText)
                .duration(recordDuration)
                .build();
    }


}
