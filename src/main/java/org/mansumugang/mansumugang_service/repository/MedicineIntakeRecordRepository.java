package org.mansumugang.mansumugang_service.repository;

import org.mansumugang.mansumugang_service.domain.medicine.Medicine;
import org.mansumugang.mansumugang_service.domain.medicine.MedicineInTakeTime;
import org.mansumugang.mansumugang_service.domain.medicine.MedicineIntakeDay;
import org.mansumugang.mansumugang_service.domain.medicine.MedicineIntakeRecord;
import org.mansumugang.mansumugang_service.dto.medicine.MedicineSummaryInfoResult;
import org.mansumugang.mansumugang_service.dto.medicine.TodayMedicineScheduleResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MedicineIntakeRecordRepository extends JpaRepository<MedicineIntakeRecord, Long> {
    void deleteAllByMedicine(Medicine foundMedicine);

    void deleteByMedicineIntakeDay(MedicineIntakeDay foundMedicineIntakeDay);

    void deleteByMedicineInTakeTime(MedicineInTakeTime medicineInTakeTime);

    Optional<MedicineIntakeRecord> findByMedicineAndMedicineIntakeDayAndMedicineInTakeTimeAndScheduledIntakeDate(
            Medicine medicine,
            MedicineIntakeDay medicineIntakeDay,
            MedicineInTakeTime medicineInTakeTime,
            LocalDate scheduledIntakeDate);

    @Query("SELECT new org.mansumugang.mansumugang_service.dto.medicine.MedicineSummaryInfoResult(" +
                "mir.status, " +
                "mit.medicineIntakeTime, " +
                "m.id, " +
                "m.medicineImageName," +
                "m.hospitalName," +
                " m.medicineDescription, " +
                "m.medicineName) " +
            "FROM MedicineIntakeRecord mir " +
            "RIGHT JOIN mir.medicineInTakeTime mit " +
            "ON " +
            "   mir.medicineInTakeTime.id = mit.id AND " +
            "   mir.scheduledIntakeDate = :targetDate" +
            " JOIN Medicine m " +
            "ON m.id = mit.medicine.id " +
            "WHERE FUNCTION('DATE', m.createdAt) <= :targetDate " +
            "AND m.intakeStopDate >= :targetDate " +
            "AND m.id IN (SELECT mid.medicine.id FROM MedicineIntakeDay mid " +
            "WHERE mid.patient.id = :patientUserId AND mid.day = :day)")
    List<MedicineSummaryInfoResult> findMedicineScheduleByDate(
            @Param("targetDate") LocalDate targetDate,
            @Param("patientUserId") Long patientUserId,
            @Param("day") DayOfWeek day);

    @Query("SELECT new org.mansumugang.mansumugang_service.dto.medicine.TodayMedicineScheduleResult(" +
            ":targetDate, " +
            "m, " +
            "mir, " +
            "mit) " +
            "FROM MedicineIntakeRecord mir " +
            "RIGHT JOIN mir.medicineInTakeTime mit " +
            "ON " +
            "   mir.medicineInTakeTime.id = mit.id AND " +
            "   mir.scheduledIntakeDate = :targetDate" +
            " JOIN Medicine m " +
            "ON m.id = mit.medicine.id " +
            "WHERE FUNCTION('DATE', m.createdAt) <= :targetDate " +
            "AND m.intakeStopDate >= :targetDate " +
            "AND m.id IN (SELECT mid.medicine.id FROM MedicineIntakeDay mid " +
            "WHERE mid.day = :day)")
    List<TodayMedicineScheduleResult> findAllPatientMedicineScheduleDyDate(
            @Param("targetDate") LocalDate targetDate,
            @Param("day") DayOfWeek day
    );

}
