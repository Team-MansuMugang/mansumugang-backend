package org.mansumugang.mansumugang_service.service.medicine;

import com.google.firebase.messaging.Message;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mansumugang.mansumugang_service.constant.ErrorType;
import org.mansumugang.mansumugang_service.constant.InternalErrorType;
import org.mansumugang.mansumugang_service.constant.MedicineRecordStatusType;
import org.mansumugang.mansumugang_service.constant.MedicineStatusType;
import org.mansumugang.mansumugang_service.domain.fcm.FcmToken;
import org.mansumugang.mansumugang_service.domain.hospital.Hospital;
import org.mansumugang.mansumugang_service.domain.medicine.MedicineIntakeDay;
import org.mansumugang.mansumugang_service.domain.medicine.MedicineIntakeRecord;
import org.mansumugang.mansumugang_service.domain.user.Patient;
import org.mansumugang.mansumugang_service.domain.user.Protector;
import org.mansumugang.mansumugang_service.dto.medicine.TodayMedicineScheduleResult;
import org.mansumugang.mansumugang_service.exception.CustomErrorException;
import org.mansumugang.mansumugang_service.exception.InternalErrorException;
import org.mansumugang.mansumugang_service.repository.*;
import org.mansumugang.mansumugang_service.service.fcm.FcmService;
import org.mansumugang.mansumugang_service.service.hospital.HospitalService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledTasks {

    private final MedicineCommonService medicineCommonService;
    private final HospitalService hospitalService;
    private final FcmService fcmService;

    private final MedicineIntakeRecordRepository medicineIntakeRecordRepository;
    private final MedicineIntakeDayRepository medicineIntakeDayRepository;
    private final FcmTokenRepository fcmTokenRepository;
    private final HospitalRepository hospitalRepository;

    @Scheduled(fixedRate = 600000)
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
                sendPushNoTakenMedicineNotificationToProtector(medicineInfo);
            }

            // 약의 복용상태가 NO_TAKEN이고, push알림을 보내지 않은 경우(즉, MedicineIntakeRecord의 status를 TRUE -> FALSE 변경한 경우)
            // isPushed를 TRUE로 변경 후 알림 전송
            if (assignedMedicineStatus == MedicineStatusType.NO_TAKEN && !medicineInfo.getMedicineIntakeRecord().getIsPushed()) {
                medicineInfo.getMedicineIntakeRecord().setIsPushed(true);
                sendPushNoTakenMedicineNotificationToProtector(medicineInfo);
            }
        }
    }

    @Scheduled(fixedRate = 600000)
    @Transactional
    protected void checkHospitalUnvisitedPatient(){
        // 병원 방문시간(HospitalVisitingTime) + 30분 시점부터 병원을 가지 않은 환자를 조회(핵심) status => false true
        // 현재시간 >=  hospitalVisitingTime + 30 && status == false  && isPushed == false

        LocalDateTime graceTime = LocalDateTime.now().minusMinutes(31);

        // 1. 필요한 쿼리문 : hospital.status 가 false 인 모든 튜플을 추출(조건 : (hospital_visiting_time + 30분 - 현재시간)의 결과가 1분 이상  && isPushed 가 false 여야함.)
        List<Hospital> hospitalUnvisitedPatientsInfos = hospitalRepository.findByStatusAndIsPushedAndHospitalVisitingTimeBefore(false, false, graceTime);


        // 2. 가져온 튜플의 patient 객체 추출 -> 보호자 찾기.
        for (Hospital hospitalUnvisitedPatientsInfo : hospitalUnvisitedPatientsInfos) {   // 환자 1, 2 , 3 -> 환자 1에대한 문자발송처리함. -> 환자 1의 보호자의 토큰을 검색함.(기존의 반환값이 리스트형임).

           sendPushHospitalUnvisitedNotificationToProtector(hospitalUnvisitedPatientsInfo);
           hospitalUnvisitedPatientsInfo.setIsPushed(true);
        }


        // 2. 알림보내기.
    }


    private void sendPushNoTakenMedicineNotificationToProtector(TodayMedicineScheduleResult medicineInfo){
        log.info("알림 전송: [환자 아이디: {}, 약 아이디: {}, 약 이름 :{}, 예정된 약 복용시간: {}]",
                medicineInfo.getMedicine().getPatient().getId(),
                medicineInfo.getMedicine().getId(),
                medicineInfo.getMedicine().getMedicineName(),
                medicineInfo.getTargetDate() + " " + medicineInfo.getMedicineInTakeTime().getMedicineIntakeTime().toString());



        // TODO: 보호자에게 약 미복용에 대한 push 알림기능 구현

        /**
         * 1. 지정된 시간에 약을 먹지않고, 유예시간(1시간) 이후에도 약을 복용하지 않은 유저를 1분 간격으로 검색. ->  checkMedicineNoTakenPatient() 메서드
         * 2. 검색된 환자의 보호자를 검색 -> 보호자의 FCM Token 조회
         * 3. FCM 백앤드로 메시지 전송
         */

        // 2번 로직.
        Patient notTakenMedicinePatient = medicineInfo.getMedicine().getPatient();

        List<FcmToken> foundTokens = fcmTokenRepository.findByProtectorId(notTakenMedicinePatient.getProtector().getId());
        log.info(String.valueOf(foundTokens.size()));

        for (FcmToken fcmToken : foundTokens) {
            Message message = Message.builder()
                    .putData("title", "약 미복용 알림")
                    .putData("body", notTakenMedicinePatient.getName() + "님께서 " +medicineInfo.getMedicine().getMedicineName() + "을 복용하지 않으셨어요!")
                    .setToken(fcmToken.getFcmToken())
                    .build();

            fcmService.sendMessage(message);
        }
    }

    private void sendPushHospitalUnvisitedNotificationToProtector(Hospital hospitalUnvisitedPatientsInfo){

        String unVisitedHospitalPatient = hospitalUnvisitedPatientsInfo.getPatient().getName();
        String hospitalName = hospitalUnvisitedPatientsInfo.getHospitalName();

        List<FcmToken> foundTokens = fcmTokenRepository.findByProtectorId(hospitalUnvisitedPatientsInfo.getPatient().getProtector().getId());


        log.info(String.valueOf(foundTokens.size()));

        for (FcmToken fcmToken : foundTokens) {
            Message message = Message.builder()
                    .putData("title", "병원 미방문 알림")
                    .putData("body", unVisitedHospitalPatient + "님께서 " + hospitalName + "에 방문하지 않으셨어요!")
                    .setToken(fcmToken.getFcmToken())
                    .build();

            fcmService.sendMessage(message);
        }
    }

}

