package org.mansumugang.mansumugang_service.service.medicine;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mansumugang.mansumugang_service.constant.ErrorType;
import org.mansumugang.mansumugang_service.constant.MedicineRecordStatusType;
import org.mansumugang.mansumugang_service.constant.MedicineStatusType;
import org.mansumugang.mansumugang_service.domain.hospital.Hospital;
import org.mansumugang.mansumugang_service.domain.medicine.Medicine;
import org.mansumugang.mansumugang_service.domain.medicine.MedicineInTakeTime;
import org.mansumugang.mansumugang_service.domain.medicine.MedicineIntakeDay;
import org.mansumugang.mansumugang_service.domain.medicine.MedicineIntakeRecord;
import org.mansumugang.mansumugang_service.domain.user.Patient;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.dto.patientScheduleToggle.PatientScheduleToggle;
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
public class PatientScheduleService {
    private final DateParser dateParser;

    private final MedicineRepository medicineRepository;
    private final MedicineIntakeDayRepository medicineIntakeDayRepository;
    private final MedicineIntakeTimeRepository medicineIntakeTimeRepository;
    private final MedicineIntakeRecordRepository medicineIntakeRecordRepository;
    private final HospitalRepository hospitalRepository;

    private final MedicineCommonService medicineCommonService;


    public PatientScheduleToggle.Dto togglePatientSchedule(User patient, PatientScheduleToggle.Request requestDto) {
        Patient validatedPatient = validatePatient(patient);

        List<PatientScheduleToggle.ToggleResultElement> toggleResult = new ArrayList<>();

        if(requestDto.getMedicine() != null) {
            PatientScheduleToggle.MedicineRequest medicineRequest = requestDto.getMedicine();
            LocalTime parsedScheduleTime = LocalTime.of(medicineRequest.getMedicineIntakeTime().getHour(), medicineRequest.getMedicineIntakeTime().getMinute());

            for (Long medicineId : medicineRequest.getMedicineIds()) {
                PatientScheduleToggle.ToggleResultElement MedicineToggleResultElement =
                        toggleMedicineIntakeStatus(medicineId, medicineRequest.getScheduledMedicineIntakeDate(), parsedScheduleTime);
                toggleResult.add(MedicineToggleResultElement);
            }
        }

        if(requestDto.getHospitalId() != null){
            PatientScheduleToggle.ToggleResultElement HospitalToggleResult = toggleHospitalStatus(validatedPatient, requestDto.getHospitalId());
            toggleResult.add(HospitalToggleResult);
        }

        return PatientScheduleToggle.Dto.fromElement(toggleResult);
    }

    private PatientScheduleToggle.ToggleResultElement toggleMedicineIntakeStatus(Long medicineId,
                                                                                 LocalDate scheduledMedicineIntakeDate,
                                                                                 LocalTime medicineIntakeTime
    ) {
        Medicine foundMedicine = medicineRepository.findById(medicineId)
                .orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchMedicineError));

        MedicineIntakeDay foundMedicineIntakeDay = medicineIntakeDayRepository.findByMedicineAndDay(
                foundMedicine, scheduledMedicineIntakeDate.getDayOfWeek()
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
                scheduledMedicineIntakeDate
        );

        if (foundMedicineIntakeRecord.isPresent()) {
            MedicineStatusType assginedMedicineStatusType = medicineCommonService.assignMedicineStatus(
                    foundMedicineIntakeRecord.get().getStatus(),
                    LocalDateTime.of(scheduledMedicineIntakeDate, medicineIntakeTime));

            if (foundMedicineIntakeRecord.get().getStatus() == MedicineRecordStatusType.TRUE) {
                foundMedicineIntakeRecord.get().setStatus(MedicineRecordStatusType.FALSE);
                foundMedicineIntakeRecord.get().setActualIntakeTime(null);
                return PatientScheduleToggle.ToggleResultElement.fromEntity(foundMedicineIntakeRecord.get());
            }

            if (foundMedicineIntakeRecord.get().getStatus() == MedicineRecordStatusType.FALSE) {
                if (assginedMedicineStatusType == MedicineStatusType.NO_TAKEN) {
                    foundMedicineIntakeRecord.get().setStatus(MedicineRecordStatusType.TRUE);
                    foundMedicineIntakeRecord.get().setActualIntakeTime(LocalDateTime.now());
                    return PatientScheduleToggle.ToggleResultElement.fromEntity(foundMedicineIntakeRecord.get());
                }
            }
        } else {
            MedicineStatusType assginedMedicineStatusType = medicineCommonService.assignMedicineStatus(
                    null,
                    LocalDateTime.of(scheduledMedicineIntakeDate, medicineIntakeTime));
            if (assginedMedicineStatusType == MedicineStatusType.WAITING) {
                MedicineIntakeRecord newMedicineIntakeRecord = medicineIntakeRecordRepository.save(MedicineIntakeRecord.createNewEntity(
                        foundMedicine,
                        foundMedicineIntakeDay,
                        foundMedicineIntakeTime,
                        scheduledMedicineIntakeDate,
                        MedicineRecordStatusType.TRUE));
                return PatientScheduleToggle.ToggleResultElement.fromEntity(newMedicineIntakeRecord);
            }

        }

        throw new CustomErrorException(ErrorType.ConditionOfNotBeingAbleToToggleError);
    }

    private PatientScheduleToggle.ToggleResultElement toggleHospitalStatus(Patient patient,
                                                                           Long hospitalId) {
        Hospital foundHospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchHospitalError));

        if(!foundHospital.getPatient().getId().equals(patient.getId())) {
            throw new CustomErrorException(ErrorType.AccessDeniedError);
        }

        if (foundHospital.getStatus()) {
            foundHospital.setStatus(false);
            foundHospital.setActualHospitalVisitingTime(null);
        } else {
            foundHospital.setStatus(true);
            foundHospital.setActualHospitalVisitingTime(LocalDateTime.now());
        }

        return PatientScheduleToggle.ToggleResultElement.fromEntity(foundHospital);
    }

    private Patient validatePatient(User user) {
        if (user == null) {
            throw new CustomErrorException(ErrorType.UserNotFoundError);
        }

        if (user instanceof Patient) {
            return (Patient) user;
        }

        throw new CustomErrorException(ErrorType.AccessDeniedError);
    }

}
