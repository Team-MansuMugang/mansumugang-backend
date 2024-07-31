package org.mansumugang.mansumugang_service.service.medicine;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mansumugang.mansumugang_service.constant.ErrorType;
import org.mansumugang.mansumugang_service.domain.medicine.Medicine;
import org.mansumugang.mansumugang_service.domain.medicine.MedicineInTakeTime;
import org.mansumugang.mansumugang_service.domain.medicine.MedicineIntakeDay;
import org.mansumugang.mansumugang_service.domain.user.Patient;
import org.mansumugang.mansumugang_service.domain.user.Protector;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.dto.medicine.MedicineSchedule;
import org.mansumugang.mansumugang_service.dto.medicine.MedicineSummaryInfoDto;
import org.mansumugang.mansumugang_service.dto.medicine.medicineDelete.MedicineDeleteRequestDto;
import org.mansumugang.mansumugang_service.dto.medicine.MedicineSave;
import org.mansumugang.mansumugang_service.dto.medicine.medicineUpdate.MedicineUpdateRequestDto;
import org.mansumugang.mansumugang_service.exception.CustomErrorException;
import org.mansumugang.mansumugang_service.exception.CustomNotValidErrorException;
import org.mansumugang.mansumugang_service.repository.*;
import org.mansumugang.mansumugang_service.utils.DateParser;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MedicineService {
    private final MedicineRepository medicineRepository;
    private final MedicineIntakeDayRepository medicineIntakeDayRepository;
    private final MedicineIntakeTimeRepository medicineIntakeTimeRepository;
    private final MedicineIntakeRecordRepository medicineIntakeRecordRepository;

    private final DateParser dateParser;

    private final PatientRepository patientRepository;

    public MedicineSchedule.Dto getMedicineByDate(User user, Long patientId, String targetDateStr) {
        Protector validProtector = validateProtector(user);
        Patient foundPatient = findPatient(patientId);
        checkUserIsProtectorOfPatient(validProtector, foundPatient);

        LocalDate parsedTargetDate = dateParser.parseDate(targetDateStr);

        List<MedicineSummaryInfoDto> medicineDayInfos =
                medicineIntakeRecordRepository.findMedicineScheduleByDate(parsedTargetDate, patientId, parsedTargetDate.getDayOfWeek());


        Map<LocalTime, List<MedicineSummaryInfoDto>> collect = medicineDayInfos.stream()
                .collect(Collectors.groupingBy(MedicineSummaryInfoDto::getMedicineIntakeTime));

        List<MedicineSchedule.Element> elements = new ArrayList<>();
        collect.forEach((localTime, medicineSummaryInfos) -> elements.add(MedicineSchedule.Element.of(localTime, medicineSummaryInfos)));
        return MedicineSchedule.Dto.of(parsedTargetDate, elements);
    }

    public void saveMedicine(User user, MedicineSave.Request requestDto) {
        Protector validProtector = validateProtector(user);
        Patient foundPatient = findPatient(requestDto.getPatientId());
        checkUserIsProtectorOfPatient(validProtector, foundPatient);

        LocalDate parsedMedicineIntakeStopDay = dateParser.parseDate(requestDto.getMedicineIntakeStopDay());
        if (!parsedMedicineIntakeStopDay.isAfter(LocalDate.now())) {
            throw new CustomNotValidErrorException("medicineIntakeStopDay", "약 복용 중단일자는 금일 이후여야 합니다.");
        }
        validateMedicineIntakeTimes(requestDto.getMedicineIntakeTimes());

        // TODO: 사진 이미지 등록필요
        Medicine newMedicine = medicineRepository.save(Medicine.of(foundPatient, requestDto, parsedMedicineIntakeStopDay, null));
        saveMedicineIntakeDays(newMedicine, foundPatient, requestDto.getMedicineIntakeDays());
        saveMedicineIntakeTimes(newMedicine, requestDto.getMedicineIntakeTimes());
    }

    public void updateMedicine(User user, Long medicineId, MedicineUpdateRequestDto requestDto) {
        Protector validProtector = validateProtector(user);
        Patient foundPatient = findPatient(requestDto.getPatientId());
        checkUserIsProtectorOfPatient(validProtector, foundPatient);

        Medicine foundMedicine = medicineRepository.findById(medicineId)
                .orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchMedicineError));

        if (requestDto.getMedicineName() != null) {
            foundMedicine.setMedicineName(requestDto.getMedicineName());
        }

        if (requestDto.getHospitalName() != null) {
            foundMedicine.setHospitalName(requestDto.getHospitalName());
        }

        if (requestDto.getMedicineDescription() != null) {
            foundMedicine.setMedicineDescription(requestDto.getMedicineDescription());
        }

        if (requestDto.getMedicineIntakeStopDay() != null) {
            LocalDate parsedMedicineIntakeStopDay = dateParser.parseDate(requestDto.getMedicineIntakeStopDay());
            if (!parsedMedicineIntakeStopDay.isAfter(LocalDate.now())) {
                throw new CustomNotValidErrorException("medicineIntakeStopDay", "약 복용 중단일자는 금일 이후여야 합니다.");
            }

            foundMedicine.setIntakeStopDate(parsedMedicineIntakeStopDay);
        }

        if (requestDto.getMedicineIntakeTimes() != null) {
            validateMedicineIntakeTimes(requestDto.getMedicineIntakeTimes());

            List<MedicineInTakeTime> foundMedicineInTakeTimes = medicineIntakeTimeRepository.findAllByMedicine(foundMedicine);
            foundMedicineInTakeTimes.forEach(medicineIntakeRecordRepository::deleteByMedicineInTakeTime);
            medicineIntakeTimeRepository.deleteAll(foundMedicineInTakeTimes);

            saveMedicineIntakeTimes(foundMedicine, requestDto.getMedicineIntakeTimes());
        }

        if (requestDto.getMedicineIntakeDays() != null) {
            List<MedicineIntakeDay> foundMedicineInTakeDays = medicineIntakeDayRepository.findAllByMedicine(foundMedicine);
            foundMedicineInTakeDays.forEach(medicineIntakeRecordRepository::deleteByMedicineIntakeDay);
            medicineIntakeDayRepository.deleteAll(foundMedicineInTakeDays);

            saveMedicineIntakeDays(foundMedicine, foundPatient, requestDto.getMedicineIntakeDays());
        }
    }

    public void deleteMedicine(User user, Long medicineId, MedicineDeleteRequestDto requestDto) {
        Protector validProtector = validateProtector(user);
        Patient foundPatient = findPatient(requestDto.getPatientId());
        checkUserIsProtectorOfPatient(validProtector, foundPatient);

        Medicine foundMedicine = medicineRepository.findById(medicineId)
                .orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchMedicineError));

        medicineIntakeRecordRepository.deleteAllByMedicine(foundMedicine);
        medicineIntakeTimeRepository.deleteAllByMedicine(foundMedicine);
        medicineIntakeDayRepository.deleteAllByMedicine(foundMedicine);
        medicineRepository.delete(foundMedicine);
    }


    private void saveMedicineIntakeDays(Medicine medicine, Patient patient, MedicineSave.MedicineIntakeDay medicineIntakeDay) {

        Map<DayOfWeek, Boolean> daysMap = new LinkedHashMap<>();
        daysMap.put(DayOfWeek.MONDAY, medicineIntakeDay.getMonday());
        daysMap.put(DayOfWeek.TUESDAY, medicineIntakeDay.getTuesday());
        daysMap.put(DayOfWeek.WEDNESDAY, medicineIntakeDay.getWednesday());
        daysMap.put(DayOfWeek.THURSDAY, medicineIntakeDay.getThursday());
        daysMap.put(DayOfWeek.FRIDAY, medicineIntakeDay.getFriday());
        daysMap.put(DayOfWeek.SATURDAY, medicineIntakeDay.getSaturday());
        daysMap.put(DayOfWeek.SUNDAY, medicineIntakeDay.getSunday());

        daysMap.forEach((dayType, shouldAdd) -> {
            if (shouldAdd) {
                medicineIntakeDayRepository.save(MedicineIntakeDay.of(medicine, patient, dayType));
            }
        });
    }

    private void validateMedicineIntakeTimes(List<LocalTime> medicineIntakeTimes) {
        Set<String> MedicineIntakeTimeSet = new HashSet<>();

        medicineIntakeTimes.forEach(
                medicineIntakeTime -> {
                    String time = Integer.toString(medicineIntakeTime.getHour()) + Integer.toString(medicineIntakeTime.getMinute());
                    if (!MedicineIntakeTimeSet.add(time))
                        throw new CustomNotValidErrorException("medicineIntakeTime", "중복된 시간이 존재합니다.");
                }
        );
    }

    private void saveMedicineIntakeTimes(Medicine medicine, List<LocalTime> medicineIntakeTimes) {
        medicineIntakeTimes.forEach(
                intakeTime -> medicineIntakeTimeRepository.save(MedicineInTakeTime.of(medicine, intakeTime))
        );
    }

    private Patient findPatient(Long patientId) {
        return patientRepository.findById(patientId).orElseThrow(() -> new CustomErrorException(ErrorType.UserNotFoundError));
    }

    private Protector validateProtector(User user) {
        if (user == null) {
            throw new CustomErrorException(ErrorType.UserNotFoundError);
        }

        if (user instanceof Protector) {
            return (Protector) user;
        }

        throw new CustomErrorException(ErrorType.AccessDeniedError);
    }

    private void checkUserIsProtectorOfPatient(Protector targetProtector, Patient patient) {
        // TODO: equals, hashcode 구현
        if (!patient.getProtector().getUsername().equals(targetProtector.getUsername())) {
            throw new CustomErrorException(ErrorType.AccessDeniedError);
        }
    }
}
