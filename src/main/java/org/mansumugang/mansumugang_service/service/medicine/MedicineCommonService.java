package org.mansumugang.mansumugang_service.service.medicine;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mansumugang.mansumugang_service.constant.MedicineRecordStatusType;
import org.mansumugang.mansumugang_service.constant.MedicineStatusType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class MedicineCommonService {
    // 약의 상태를 정의하는 메소드
    public MedicineStatusType assignMedicineStatus(MedicineRecordStatusType medicineRecordStatusType, LocalDateTime medicineIntakeTime) {
        // 약 등록 시 이미 지나간 복용시간일 경우
        if(medicineRecordStatusType == MedicineRecordStatusType.PASS){
            return MedicineStatusType.PASS;
        }

        // 약을 복용할 시간이 아닌경우
        if(medicineIntakeTime.isAfter(LocalDateTime.now())){
            return MedicineStatusType.NOT_TIME;

        // 약을 복용해야할 시간인 경우
        }else {
            // 현재 1시간의 유예시간에 속하는 경우
            if(medicineRecordStatusType == null){
                return MedicineStatusType.WAITING;

            // 약 복용을 한 경우
            } else if(medicineRecordStatusType == MedicineRecordStatusType.TRUE) {
                return MedicineStatusType.TAKEN;

            // 약 복용을 하지 않았거나 사용자가 약을 복용하지 않았다고 (medicine toggle api를 이용하여) 명시한 경우
            } else {
                return MedicineStatusType.NO_TAKEN;
            }
        }
    }
}
