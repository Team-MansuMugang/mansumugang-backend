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
@Slf4j
public class MedicineCommonService {
    // 약의 상태를 정의하는 메소드
    public MedicineStatusType assignMedicineStatus(MedicineRecordStatusType medicineRecordStatusType, LocalDateTime medicineIntakeTime) {
        log.info(medicineIntakeTime.toString());
        log.info(LocalDateTime.now().toString());

        if(medicineRecordStatusType == MedicineRecordStatusType.PASS){
            return MedicineStatusType.PASS;
        }

        if(medicineIntakeTime.isAfter(LocalDateTime.now())){
            return MedicineStatusType.NOT_TIME;
        }else {
            if(medicineRecordStatusType == null){
                return MedicineStatusType.WAITING;
            } else if(medicineRecordStatusType == MedicineRecordStatusType.TRUE) {
                return MedicineStatusType.TAKEN;
            } else {
                return MedicineStatusType.NO_TAKEN;
            }
        }
    }
}
