package org.mansumugang.mansumugang_service.service.schedule;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mansumugang.mansumugang_service.domain.hospital.Hospital;
import org.mansumugang.mansumugang_service.domain.user.Patient;
import org.mansumugang.mansumugang_service.domain.user.Protector;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.dto.medicine.MedicineSummaryInfo;
import org.mansumugang.mansumugang_service.dto.medicine.MedicineSummaryInfoResult;
import org.mansumugang.mansumugang_service.repository.*;
import org.mansumugang.mansumugang_service.service.medicine.MedicineCommonService;
import org.mansumugang.mansumugang_service.service.user.UserCommonService;
import org.mansumugang.mansumugang_service.utils.DateParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ScheduleService {
    @Value("${file.upload.image.api}")
    private String imageApiUrl;

    private final DateParser dateParser;
    private final MedicineIntakeRecordRepository medicineIntakeRecordRepository;

    private final UserCommonService userCommonService;

    private final HospitalRepository hospitalRepository;
    private final MedicineCommonService medicineCommonService;


    public MedicineSummaryInfo.Dto getPatientScheduleByDate(User user, Long patientId, String targetDateStr) {
        Protector foundProtector = userCommonService.findProtector(user);
        Patient foundPatient = userCommonService.findPatient(patientId);
        userCommonService.checkUserIsProtectorOfPatient(foundProtector, foundPatient);

        LocalDate parsedTargetDate = dateParser.parseDate(targetDateStr);

        return readUserSchedule(parsedTargetDate, foundPatient.getId());
    }

    public MedicineSummaryInfo.Dto getPatientScheduleByDate(User user, String targetDateStr) {
        Patient foundProtector = userCommonService.findPatient(user);

        LocalDate parsedTargetDate = dateParser.parseDate(targetDateStr);

        return readUserSchedule(parsedTargetDate, foundProtector.getId());
    }

    // 특정일에 대한 환자의 약 정보 조회
    public MedicineSummaryInfo.Dto readUserSchedule(LocalDate targetDate, Long patientId) {
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

        // 특정 환자가 금일 방문 해야 할 병원 일정 조회
        List<Hospital> hospitalScheduleResult = hospitalRepository.findAllByPatientIdAndHospitalVisitingTimeBetween(patientId,
                LocalDateTime.of(targetDate, LocalTime.of(0, 0)),
                LocalDateTime.of(targetDate, LocalTime.of(23, 59)));

        // 같은 방문시간을 가진 병원끼리 그룹화
        Map<LocalTime, Hospital> hospitalByTime = hospitalScheduleResult.stream()
                .collect(Collectors.toMap(
                        hospital -> hospital.getHospitalVisitingTime().toLocalTime(), // Extract LocalTime
                        hospital -> hospital // Hospital object itself
                ));


        // 모든 LocalTime 키를 통합
        Set<LocalTime> allTimes = new TreeSet<>();
        allTimes.addAll(hospitalByTime.keySet());
        allTimes.addAll(medicineSummaryInfoByTime.keySet());

        // 결과를 저장할 TreeMap (자동으로 키 기준으로 정렬됨)
        Map<LocalTime, MedicineSummaryInfo.ScheduleElement> scheduleMap = new TreeMap<>();

        // 모든 LocalTime에 대해 CombinedInfo 생성
        for (LocalTime time : allTimes) {
            Hospital hospital = hospitalByTime.get(time);
            List<MedicineSummaryInfo.MedicineSummaryInfoElement> medicines = medicineSummaryInfoByTime.getOrDefault(time, Collections.emptyList());
            MedicineSummaryInfo.ScheduleElement combinedInfo = new MedicineSummaryInfo.ScheduleElement(medicines, hospital);
            scheduleMap.put(time, combinedInfo);
        }

        return MedicineSummaryInfo.Dto.of(imageApiUrl, targetDate, MedicineSummaryInfo.TimeElement.convertTimeElements(scheduleMap), patientId);
    }
}
