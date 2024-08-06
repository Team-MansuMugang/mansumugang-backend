package org.mansumugang.mansumugang_service.service.medicine;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mansumugang.mansumugang_service.constant.ErrorType;
import org.mansumugang.mansumugang_service.constant.MedicineRecordStatusType;
import org.mansumugang.mansumugang_service.constant.MedicineStatusType;
import org.mansumugang.mansumugang_service.domain.medicine.Medicine;
import org.mansumugang.mansumugang_service.domain.medicine.MedicineInTakeTime;
import org.mansumugang.mansumugang_service.domain.medicine.MedicineIntakeDay;
import org.mansumugang.mansumugang_service.domain.medicine.MedicineIntakeRecord;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.dto.medicineIntake.MedicineIntakeToggle;
import org.mansumugang.mansumugang_service.exception.CustomErrorException;
import org.mansumugang.mansumugang_service.repository.*;
import org.mansumugang.mansumugang_service.utils.DateParser;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MedicineIntakeService {
    private final DateParser dateParser;

    private final MedicineRepository medicineRepository;
    private final MedicineIntakeDayRepository medicineIntakeDayRepository;
    private final MedicineIntakeTimeRepository medicineIntakeTimeRepository;
    private final MedicineIntakeRecordRepository medicineIntakeRecordRepository;

    private final MedicineCommonService medicineCommonService;


    public MedicineIntakeToggle.Dto toggleMedicineIntakeStatus(User patient, MedicineIntakeToggle.Request requestDto) {
        if (patient == null) {
            throw new CustomErrorException(ErrorType.UserNotFoundError);
        }

        List<MedicineIntakeToggle.ToggleResultElement> toggleResult = new ArrayList<>();
        for (Long medicineId : requestDto.getMedicineIds()) {
            MedicineIntakeToggle.ToggleResultElement toggleResultElement = _toggleMedicineIntakeStatus(medicineId, requestDto.getMedicineIntakeTime(), requestDto.getScheduledMedicineIntakeDate());
            toggleResult.add(toggleResultElement);
        }

        return MedicineIntakeToggle.Dto.fromElement(toggleResult);
    }

    private MedicineIntakeToggle.ToggleResultElement _toggleMedicineIntakeStatus(Long medicineId, LocalTime medicineIntakeTime, String scheduledMedicineIntakeDate) {
        LocalDate parsedScheduledMedicineIntakeDate = dateParser.parseDate(scheduledMedicineIntakeDate);
        LocalTime parsedIntakeTime = LocalTime.of(medicineIntakeTime.getHour(), medicineIntakeTime.getMinute());

        Medicine foundMedicine = medicineRepository.findById(medicineId)
                .orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchMedicineError));

        MedicineIntakeDay foundMedicineIntakeDay = medicineIntakeDayRepository.findByMedicineAndDay(
                foundMedicine, parsedScheduledMedicineIntakeDate.getDayOfWeek()
        ).orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchMedicineIntakeDayError));

        MedicineInTakeTime foundMedicineIntakeTime = medicineIntakeTimeRepository.findByMedicineAndMedicineIntakeTime(
                foundMedicine,
                LocalTime.of(
                        medicineIntakeTime.getHour(),
                        medicineIntakeTime.getMinute())
        ).orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchMedicineIntakeTimeError));

        Optional<MedicineIntakeRecord> foundMedicineIntakeRecord = medicineIntakeRecordRepository.findByMedicineAndMedicineIntakeDayAndMedicineInTakeTimeAndScheduledIntakeDate(
                foundMedicine,
                foundMedicineIntakeDay,
                foundMedicineIntakeTime,
                parsedScheduledMedicineIntakeDate
        );

        if (foundMedicineIntakeRecord.isPresent()) {
            MedicineStatusType assginedMedicineStatusType = medicineCommonService.assignMedicineStatus(
                    foundMedicineIntakeRecord.get().getStatus(),
                    LocalDateTime.of(parsedScheduledMedicineIntakeDate, parsedIntakeTime));

            if (foundMedicineIntakeRecord.get().getStatus() == MedicineRecordStatusType.TRUE) {
                foundMedicineIntakeRecord.get().setStatus(MedicineRecordStatusType.FALSE);
                foundMedicineIntakeRecord.get().setActualIntakeTime(null);
                return MedicineIntakeToggle.ToggleResultElement.fromEntity(foundMedicineIntakeRecord.get());
            }

            if (foundMedicineIntakeRecord.get().getStatus() == MedicineRecordStatusType.FALSE) {
                if (assginedMedicineStatusType == MedicineStatusType.NO_TAKEN) {
                    foundMedicineIntakeRecord.get().setStatus(MedicineRecordStatusType.TRUE);
                    foundMedicineIntakeRecord.get().setActualIntakeTime(LocalDateTime.now());
                    return MedicineIntakeToggle.ToggleResultElement.fromEntity(foundMedicineIntakeRecord.get());
                }
            }
        } else {
            MedicineStatusType assginedMedicineStatusType = medicineCommonService.assignMedicineStatus(
                    null,
                    LocalDateTime.of(parsedScheduledMedicineIntakeDate, parsedIntakeTime));
            if (assginedMedicineStatusType == MedicineStatusType.WAITING) {
                MedicineIntakeRecord newMedicineIntakeRecord = medicineIntakeRecordRepository.save(MedicineIntakeRecord.createNewEntity(
                        foundMedicine,
                        foundMedicineIntakeDay,
                        foundMedicineIntakeTime,
                        parsedScheduledMedicineIntakeDate,
                        MedicineRecordStatusType.TRUE));
                return MedicineIntakeToggle.ToggleResultElement.fromEntity(newMedicineIntakeRecord);
            }

        }

        throw new CustomErrorException(ErrorType.ConditionOfNotBeingAbleToToggleError);
    }



}
