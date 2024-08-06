package org.mansumugang.mansumugang_service.service.medicine;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mansumugang.mansumugang_service.constant.InternalErrorType;
import org.mansumugang.mansumugang_service.constant.MedicineRecordStatusType;
import org.mansumugang.mansumugang_service.constant.MedicineStatusType;
import org.mansumugang.mansumugang_service.domain.medicine.MedicineIntakeDay;
import org.mansumugang.mansumugang_service.domain.medicine.MedicineIntakeRecord;
import org.mansumugang.mansumugang_service.dto.medicine.TodayMedicineScheduleResult;
import org.mansumugang.mansumugang_service.exception.InternalErrorException;
import org.mansumugang.mansumugang_service.repository.MedicineIntakeDayRepository;
import org.mansumugang.mansumugang_service.repository.MedicineIntakeRecordRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledTasks {
    private final MedicineIntakeRecordRepository medicineIntakeRecordRepository;
    ;
    private final MedicineIntakeDayRepository medicineIntakeDayRepository;
    private final MedicineCommonService medicineCommonService;

    @Scheduled(fixedRate = 60000)
    @Transactional
    protected void checkMedicineNoTakenPatient() {
        // 오늘과 어제 각각에 대해 먹어야하는 약의 리스트를 조회
        List<TodayMedicineScheduleResult> allPatientMedicineScheduleOnYesterday = medicineIntakeRecordRepository.findAllPatientMedicineScheduleDyDate(
                LocalDate.now().minusDays(1),
                LocalDate.now().minusDays(1).getDayOfWeek());
        List<TodayMedicineScheduleResult> allPatientMedicineScheduleOnToday = medicineIntakeRecordRepository.findAllPatientMedicineScheduleDyDate(
                LocalDate.now(),
                LocalDate.now().getDayOfWeek());

        // 조회한 두개의 리스트를 병합
        List<TodayMedicineScheduleResult> combinedMedicineSchedule = new ArrayList<>(allPatientMedicineScheduleOnYesterday);
        combinedMedicineSchedule.addAll(allPatientMedicineScheduleOnToday);

        for (TodayMedicineScheduleResult medicineInfo : combinedMedicineSchedule) {
            // 각각의 약 약 복용일정에 대해 현재 상태를 얻음
            MedicineStatusType assignedMedicineStatus = medicineCommonService.assignMedicineStatus(
                    medicineInfo.getMedicineIntakeRecord() != null ?  medicineInfo.getMedicineIntakeRecord().getStatus() : null,
                    LocalDateTime.of(
                            medicineInfo.getTargetDate(),
                            medicineInfo.getMedicineInTakeTime().getMedicineIntakeTime()
                    )
            );

            // 만약 현재 상태가 WAITING이고 약 복용시간의 한시간이 지났을 경우
            // 약의 상태를 FALSE로 변경 후 알림 전송
            if (assignedMedicineStatus == MedicineStatusType.WAITING &&
                    LocalDateTime.now().isAfter(LocalDateTime.of(medicineInfo.getTargetDate(), medicineInfo.getMedicineInTakeTime().getMedicineIntakeTime()).plusHours(1))) {
                MedicineIntakeDay foundMedicineIntakeDay = medicineIntakeDayRepository.findByMedicineAndDay(medicineInfo.getMedicine(), medicineInfo.getTargetDate().getDayOfWeek())
                        .orElseThrow(() -> new InternalErrorException(InternalErrorType.NoSuchMedicineIntakeDayError));

                medicineIntakeRecordRepository.save(MedicineIntakeRecord.createNewEntity(
                                medicineInfo.getMedicine(),
                                foundMedicineIntakeDay,
                                medicineInfo.getMedicineInTakeTime(),
                                medicineInfo.getTargetDate(),
                                MedicineRecordStatusType.FALSE,
                                true
                        )
                );
               sendPushNotificationToProtector(medicineInfo);
            }

            // 약의 복용상태가 NO_TAKEN이고, push알림을 보내지 않은 경우(즉, MedicineIntakeRecord의 status를 TRUE -> FALSE 변경한 경우)
            // isPushed를 TRUE로 변경 후 알림 전송
            if (assignedMedicineStatus == MedicineStatusType.NO_TAKEN && !medicineInfo.getMedicineIntakeRecord().getIsPushed()) {
                medicineInfo.getMedicineIntakeRecord().setIsPushed(true);
                sendPushNotificationToProtector(medicineInfo);
            }
        }
    }

    private void sendPushNotificationToProtector(TodayMedicineScheduleResult medicineInfo){
        log.info("알림 전송: [환자 아이디: {}, 약 아이디: {}, 약 이름 :{}, 예정된 약 복용시간: {}]",
                medicineInfo.getMedicine().getPatient().getId(),
                medicineInfo.getMedicine().getId(),
                medicineInfo.getMedicine().getMedicineName(),
                medicineInfo.getTargetDate() + " " + medicineInfo.getMedicineInTakeTime().getMedicineIntakeTime().toString());

        // TODO: 보호자에게 약 미복용에 대한 push 알림기능 구현
    }
}