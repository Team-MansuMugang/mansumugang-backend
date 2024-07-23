package org.mansumugang.mansumugang_service.service.medicine;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mansumugang.mansumugang_service.constant.ErrorType;
import org.mansumugang.mansumugang_service.domain.medicine.Medicine;
import org.mansumugang.mansumugang_service.domain.medicine.MedicineInTakeTime;
import org.mansumugang.mansumugang_service.domain.medicine.MedicineIntakeDay;
import org.mansumugang.mansumugang_service.domain.medicine.MedicineIntakeRecord;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.dto.medicineIntake.MedicineIntakeToggleDto;
import org.mansumugang.mansumugang_service.dto.medicineIntake.MedicineIntakeToggleRequestDto;
import org.mansumugang.mansumugang_service.exception.CustomErrorException;
import org.mansumugang.mansumugang_service.repository.*;
import org.mansumugang.mansumugang_service.utils.DateParser;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MedicineIntakeService {
    private final MedicineRepository medicineRepository;
    private final MedicineIntakeDayRepository medicineIntakeDayRepository;
    private final MedicineIntakeTimeRepository medicineIntakeTimeRepository;
    private final MedicineIntakeRecordRepository medicineIntakeRecordRepository;

    private final PatientRepository patientRepository;

    private final DateParser dateParser;

    public MedicineIntakeToggleDto toggleMedicineIntakeStatus(User patient, MedicineIntakeToggleRequestDto requestDto){
        if(patient == null){
            throw new CustomErrorException(ErrorType.UserNotFoundError);
        }

        LocalDate parsedScheduledMedicineIntakeDate = dateParser.parseDate(requestDto.getScheduledMedicineIntakeDate());
        LocalTime parsedIntakeTime = LocalTime.of(requestDto.getMedicineIntakeTime().getMedicineIntakeHours(), requestDto.getMedicineIntakeTime().getMedicineIntakeMinutes());

        Medicine foundMedicine = medicineRepository.findById(requestDto.getMedicineId())
                .orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchMedicineError));

        if(parsedScheduledMedicineIntakeDate.isBefore(foundMedicine.getCreatedAt().toLocalDate())){
            throw new CustomErrorException(ErrorType.NoMedicineIntakeRecordForDurationError);
        }
        if(parsedScheduledMedicineIntakeDate.isAfter(foundMedicine.getIntakeStopDate())){
            throw new CustomErrorException(ErrorType.NoMedicineIntakeRecordForDurationError);
        }
        if(LocalDateTime.of(parsedScheduledMedicineIntakeDate, parsedIntakeTime).isAfter(LocalDateTime.now())) {
            throw new CustomErrorException(ErrorType.NonDosageTimeError);
        }

        MedicineIntakeDay foundMedicineIntakeDay = medicineIntakeDayRepository.findByMedicineAndDay(
                foundMedicine,parsedScheduledMedicineIntakeDate.getDayOfWeek()
                ).orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchMedicineIntakeDayError));

        MedicineInTakeTime foundMedicineIntakeTime = medicineIntakeTimeRepository.findByMedicineAndMedicineIntakeTime(
                        foundMedicine,
                        LocalTime.of(
                                requestDto.getMedicineIntakeTime().getMedicineIntakeHours(),
                                requestDto.getMedicineIntakeTime().getMedicineIntakeMinutes())
                        ).orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchMedicineIntakeTimeError));

        Optional<MedicineIntakeRecord> foundMedicineIntakeRecord = medicineIntakeRecordRepository.findByMedicineAndMedicineIntakeDayAndMedicineInTakeTimeAndScheduledIntakeDate(
                foundMedicine,
                foundMedicineIntakeDay,
                foundMedicineIntakeTime,
                parsedScheduledMedicineIntakeDate
        );

        if(foundMedicineIntakeRecord.isEmpty()){
            MedicineIntakeRecord newMedicineIntakeRecord = medicineIntakeRecordRepository.save(MedicineIntakeRecord.createNewEntity(
                    foundMedicine,
                    foundMedicineIntakeDay,
                    foundMedicineIntakeTime,
                    parsedScheduledMedicineIntakeDate));
            return MedicineIntakeToggleDto.fromEntity(newMedicineIntakeRecord);
        }

        foundMedicineIntakeRecord.get().toggle();

        return MedicineIntakeToggleDto.fromEntity(foundMedicineIntakeRecord.get());
    }
}
