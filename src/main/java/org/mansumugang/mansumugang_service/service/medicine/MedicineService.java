package org.mansumugang.mansumugang_service.service.medicine;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mansumugang.mansumugang_service.constant.ErrorType;
import org.mansumugang.mansumugang_service.constant.MedicineRecordStatusType;
import org.mansumugang.mansumugang_service.domain.medicine.Medicine;
import org.mansumugang.mansumugang_service.domain.medicine.MedicineInTakeTime;
import org.mansumugang.mansumugang_service.domain.medicine.MedicineIntakeDay;
import org.mansumugang.mansumugang_service.domain.medicine.MedicineIntakeRecord;
import org.mansumugang.mansumugang_service.domain.user.Patient;
import org.mansumugang.mansumugang_service.domain.user.Protector;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.dto.medicine.*;
import org.mansumugang.mansumugang_service.exception.CustomErrorException;
import org.mansumugang.mansumugang_service.exception.CustomNotValidErrorException;
import org.mansumugang.mansumugang_service.repository.*;
import org.mansumugang.mansumugang_service.service.fileService.FileService;
import org.mansumugang.mansumugang_service.utils.DateParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MedicineService {
    @Value("${file.upload.image.api}")
    private String imageApiUrl;
    private final DateParser dateParser;

    private final MedicineRepository medicineRepository;
    private final MedicineIntakeDayRepository medicineIntakeDayRepository;
    private final MedicineIntakeTimeRepository medicineIntakeTimeRepository;
    private final MedicineIntakeRecordRepository medicineIntakeRecordRepository;
    private final PatientRepository patientRepository;

    private final FileService fileService;
    private final MedicineCommonService medicineCommonService;

    public MedicineSummaryInfo.Dto getMedicineSummaryInfoByDate(User user, Long patientId, String targetDateStr) {
        Protector validatedProtector = validateProtector(user);
        Patient foundPatient = findPatient(patientId);
        checkUserIsProtectorOfPatient(validatedProtector, foundPatient);

        LocalDate parsedTargetDate = dateParser.parseDate(targetDateStr);

        return getMedicineSummaryInfoByDate(parsedTargetDate, foundPatient.getId());
    }

    public MedicineSummaryInfo.Dto getMedicineSummaryInfoByDate(User user, String targetDateStr) {
        Patient validatedPatient = validatePatient(user);

        LocalDate parsedTargetDate = dateParser.parseDate(targetDateStr);

        return getMedicineSummaryInfoByDate(parsedTargetDate, validatedPatient.getId());
    }

    // 특정일에 대한 환자의 약 정보 조회
    public MedicineSummaryInfo.Dto getMedicineSummaryInfoByDate(LocalDate targetDate, Long patientId) {
        // 테이터베이스로부터 특정일에 대한 환자의 약 정보 쿼리 결과
        List<MedicineSummaryInfoResult> medicineDayInfoResult =
                medicineIntakeRecordRepository.findMedicineScheduleByDate(targetDate, patientId, targetDate.getDayOfWeek());

        // 각각의 약에 대해 상태를 정의함
        List<MedicineSummaryInfo.MedicineSummaryInfoElement> medicineSummaryInfos = medicineDayInfoResult.stream().map(medicineSummaryInfoResult ->
                MedicineSummaryInfo.MedicineSummaryInfoElement.of(
                        medicineSummaryInfoResult,
                        medicineCommonService.assignMedicineStatus(
                                medicineSummaryInfoResult.getStatus(),
                                LocalDateTime.of(targetDate, medicineSummaryInfoResult.getMedicineIntakeTime())
                        )
                )
        ).toList();

        // 같은 복용시간을 가진 약끼리 그룹화
        Map<LocalTime, List<MedicineSummaryInfo.MedicineSummaryInfoElement>> medicineSummaryInfoByTime = medicineSummaryInfos.stream()
                .collect(Collectors.groupingBy(MedicineSummaryInfo.MedicineSummaryInfoElement::getMedicineIntakeTime));

        return MedicineSummaryInfo.Dto.of(imageApiUrl, targetDate, MedicineSummaryInfo.TimeElement.convertTimeElements(medicineSummaryInfoByTime));
    }

    public void saveMedicine(User user, MultipartFile medicineImage, MedicineSave.Request requestDto) {
        Protector validProtector = validateProtector(user);
        Patient foundPatient = findPatient(requestDto.getPatientId());
        checkUserIsProtectorOfPatient(validProtector, foundPatient);

        LocalDate parsedMedicineIntakeStopDay = dateParser.parseDate(requestDto.getMedicineIntakeStopDay());
        if (!parsedMedicineIntakeStopDay.isAfter(LocalDate.now())) {
            throw new CustomNotValidErrorException("medicineIntakeStopDay", "약 복용 중단일자는 금일 이후여야 합니다.");
        }
        validateMedicineIntakeTimes(requestDto.getMedicineIntakeTimes());

        String medicineImageName = null;
        if (medicineImage != null) {
            try {
                medicineImageName = fileService.saveImageFiles(medicineImage);
            } catch (Exception e) {
                throw new CustomErrorException(ErrorType.InternalServerError);
            }

        }

        Medicine newMedicine = medicineRepository.save(Medicine.of(foundPatient, requestDto, parsedMedicineIntakeStopDay, medicineImageName));
        List<MedicineIntakeDay> newMedicineIntakeDays = saveMedicineIntakeDays(newMedicine, foundPatient, requestDto.getMedicineIntakeDays());
        List<MedicineInTakeTime> newMedicineInTakeTimes = saveMedicineIntakeTimes(newMedicine, requestDto.getMedicineIntakeTimes());

        savePassedMedicineRecord(newMedicine, newMedicineIntakeDays, newMedicineInTakeTimes);


    }

    public void updateMedicine(User user, Long medicineId, MultipartFile medicineImage, MedicineUpdate.Request requestDto) {
        MedicineIntakeDay passedTargetMedicineIntakeDay = null;

        Protector validProtector = validateProtector(user);
        Patient foundPatient = findPatient(requestDto.getPatientId());
        checkUserIsProtectorOfPatient(validProtector, foundPatient);

        Medicine foundMedicine = medicineRepository.findById(medicineId)
                .orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchMedicineError));

        List<MedicineInTakeTime> renewedMedicineIntakeTimes = medicineIntakeTimeRepository.findAllByMedicine(foundMedicine);

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

        if (requestDto.getMedicineIntakeTimeToAdd() != null) {
            validateMedicineIntakeTimes(requestDto.getMedicineIntakeTimeToAdd());
            for (LocalTime newMedicineIntakeTime : requestDto.getMedicineIntakeTimeToAdd()) {
                Optional<MedicineInTakeTime> foundMedicineIntakeTime = medicineIntakeTimeRepository.findByMedicineAndMedicineIntakeTime(foundMedicine, newMedicineIntakeTime);
                if (foundMedicineIntakeTime.isPresent()) {
                    throw new CustomErrorException(ErrorType.AlreadyExistMedicineIntakeTimeError);
                }

                MedicineInTakeTime newMedicineIntakeTimeEntity = medicineIntakeTimeRepository.save(MedicineInTakeTime.of(foundMedicine, newMedicineIntakeTime));
                renewedMedicineIntakeTimes.add(newMedicineIntakeTimeEntity);
            }
        }

        if (requestDto.getMedicineIntakeTimeToDelete() != null) {
            validateMedicineIntakeTimes(requestDto.getMedicineIntakeTimeToDelete());
            for (LocalTime newMedicineIntakeTime : requestDto.getMedicineIntakeTimeToDelete()) {
                Optional<MedicineInTakeTime> foundMedicineIntakeTime = medicineIntakeTimeRepository.findByMedicineAndMedicineIntakeTime(foundMedicine, newMedicineIntakeTime);
                if (foundMedicineIntakeTime.isEmpty()) {
                    throw new CustomErrorException(ErrorType.NoSuchMedicineIntakeTimeError);
                }

                medicineIntakeRecordRepository.deleteByMedicineInTakeTime(foundMedicineIntakeTime.get());
                medicineIntakeTimeRepository.delete(foundMedicineIntakeTime.get());

                renewedMedicineIntakeTimes = renewedMedicineIntakeTimes.stream().filter(medicineInTakeTime -> !Objects.equals(medicineInTakeTime.getId(), foundMedicineIntakeTime.get().getId())).toList();
            }
        }

        if (requestDto.getMedicineIntakeDayToAdd() != null) {
            validateMedicineIntakeDays(requestDto.getMedicineIntakeDayToAdd());
            for (DayOfWeek newMedicineIntakeDay : requestDto.getMedicineIntakeDayToAdd()) {
                Optional<MedicineIntakeDay> foundMedicineIntakeDay = medicineIntakeDayRepository.findByMedicineAndDay(foundMedicine, newMedicineIntakeDay);
                if (foundMedicineIntakeDay.isPresent()) {
                    throw new CustomErrorException(ErrorType.AlreadyExistMedicineIntakeDayError);
                }

                MedicineIntakeDay newMedicineIntakeDayEntity = medicineIntakeDayRepository.save(MedicineIntakeDay.of(foundMedicine, foundPatient, newMedicineIntakeDay));

                if (newMedicineIntakeDayEntity.getDay().equals(LocalDate.now().getDayOfWeek())) {
                    passedTargetMedicineIntakeDay = newMedicineIntakeDayEntity;
                }
            }
        }

        if (requestDto.getMedicineIntakeDayToDelete() != null) {
            validateMedicineIntakeDays(requestDto.getMedicineIntakeDayToDelete());
            for (DayOfWeek newMedicineIntakeDay : requestDto.getMedicineIntakeDayToDelete()) {
                Optional<MedicineIntakeDay> foundMedicineIntakeDay = medicineIntakeDayRepository.findByMedicineAndDay(foundMedicine, newMedicineIntakeDay);
                if (foundMedicineIntakeDay.isEmpty()) {
                    throw new CustomErrorException(ErrorType.NoSuchMedicineIntakeDayError);
                }

                medicineIntakeRecordRepository.deleteByMedicineIntakeDay(foundMedicineIntakeDay.get());
                medicineIntakeDayRepository.delete(foundMedicineIntakeDay.get());
            }
        }

        if (medicineImage != null) {
            try {
                String originalMedicineImageName = foundMedicine.getMedicineImageName();
                foundMedicine.setMedicineImageName(fileService.saveImageFiles(medicineImage));
                fileService.deleteImageFile(originalMedicineImageName);
            } catch (Exception e) {
                throw new CustomErrorException(ErrorType.InternalServerError);
            }
        }

        if (passedTargetMedicineIntakeDay != null) {
            savePassedMedicineRecord(foundMedicine, List.of(passedTargetMedicineIntakeDay), renewedMedicineIntakeTimes);
        }
    }

    public void deleteMedicine(User user, Long medicineId, MedicineDelete.Request requestDto) {
        Protector validProtector = validateProtector(user);
        Patient foundPatient = findPatient(requestDto.getPatientId());
        checkUserIsProtectorOfPatient(validProtector, foundPatient);

        Medicine foundMedicine = medicineRepository.findById(medicineId)
                .orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchMedicineError));

        try {
            String originalMedicineImageName = foundMedicine.getMedicineImageName();
            fileService.deleteImageFile(originalMedicineImageName);
        } catch (Exception e) {
            throw new CustomErrorException(ErrorType.InternalServerError);
        }

        medicineIntakeRecordRepository.deleteAllByMedicine(foundMedicine);
        medicineIntakeTimeRepository.deleteAllByMedicine(foundMedicine);
        medicineIntakeDayRepository.deleteAllByMedicine(foundMedicine);
        medicineRepository.delete(foundMedicine);
    }

    private List<MedicineIntakeDay> saveMedicineIntakeDays(Medicine medicine, Patient patient, List<DayOfWeek> medicineIntakeDay) {
        validateMedicineIntakeDays(medicineIntakeDay);

        return medicineIntakeDay.stream().map(dayOfWeek -> medicineIntakeDayRepository.save(MedicineIntakeDay.of(medicine, patient, dayOfWeek))).toList();
    }

    private List<MedicineInTakeTime> saveMedicineIntakeTimes(Medicine medicine, List<LocalTime> medicineIntakeTimes) {
        return medicineIntakeTimes.stream().map(
                intakeTime -> medicineIntakeTimeRepository.save(MedicineInTakeTime.of(medicine, intakeTime))).toList();
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

    private void validateMedicineIntakeDays(List<DayOfWeek> medicineIntakeDays) {
        Set<DayOfWeek> MedicineIntakeTimeSet = new HashSet<>();

        medicineIntakeDays.forEach(
                medicineIntakeDay -> {
                    if (!MedicineIntakeTimeSet.add(medicineIntakeDay))
                        throw new CustomNotValidErrorException("medicineIntakeDay", "중복된 요일이 존재합니다.");
                }
        );
    }

    // 금일 약을 복용할 경우 현시간 이전의 약은 PASS로 처리
    private void savePassedMedicineRecord(Medicine medicine,
                                          List<MedicineIntakeDay> MedicineIntakeDays,
                                          List<MedicineInTakeTime> medicineIntakeTimes) {
        DayOfWeek todayDayOfWeek = LocalDate.now().getDayOfWeek();
        LocalTime nowTime = LocalTime.now();
        LocalDate nowDate = LocalDate.now();

        for (MedicineIntakeDay medicineIntakeDay : MedicineIntakeDays) {
            if (todayDayOfWeek.equals(medicineIntakeDay.getDay())) {
                for (MedicineInTakeTime medicineInTakeTime : medicineIntakeTimes) {
                    if (medicineInTakeTime.getMedicineIntakeTime().isBefore(nowTime)) {
                        Optional<MedicineIntakeRecord> foundMedicineIntakeRecord =
                                medicineIntakeRecordRepository.findByMedicineAndMedicineIntakeDayAndMedicineInTakeTimeAndScheduledIntakeDate(
                                        medicine,
                                        medicineIntakeDay,
                                        medicineInTakeTime,
                                        nowDate
                                );

                        if(foundMedicineIntakeRecord.isEmpty()) {
                            medicineIntakeRecordRepository.save(
                                    MedicineIntakeRecord.createNewEntity(
                                            medicine,
                                            medicineIntakeDay,
                                            medicineInTakeTime,
                                            nowDate,
                                            MedicineRecordStatusType.PASS
                                    )
                            );
                        }
                    }
                }
            }
        }
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

    private Patient validatePatient(User user) {
        if (user == null) {
            throw new CustomErrorException(ErrorType.UserNotFoundError);
        }

        if (user instanceof Patient) {
            return (Patient) user;
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
