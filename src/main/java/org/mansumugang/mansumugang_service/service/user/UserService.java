package org.mansumugang.mansumugang_service.service.user;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mansumugang.mansumugang_service.constant.ErrorType;
import org.mansumugang.mansumugang_service.domain.user.Patient;
import org.mansumugang.mansumugang_service.domain.user.Protector;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.dto.user.PatientInquiry;
import org.mansumugang.mansumugang_service.exception.CustomErrorException;
import org.mansumugang.mansumugang_service.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final PatientRepository patientRepository;

    public PatientInquiry.Dto getPatientsByProtector(User user){

        // 1. AuthenticationPrincipal 로 넘겨받은 user 가 보호자 객체인지 검증
        Protector validProtector = validateProtector(user);

        // 2. 검증된 보호자 객체로 보호자의 환자 찾기.
        List<Patient> foundPatients = getAllPatientsByProtectorId(validProtector);

        return PatientInquiry.Dto.fromEntity(foundPatients);

    }



    private Protector validateProtector(User user) {

        if (user == null){
            throw new CustomErrorException(ErrorType.UserNotFoundError);
        }

        if(user instanceof Protector){
            return (Protector) user;
        }

        throw new CustomErrorException(ErrorType.AccessDeniedError);
    }


    private List<Patient> getAllPatientsByProtectorId(Protector validProtector) {
        List<Patient> foundPatients = patientRepository.findByProtector_id(validProtector.getId());

        if (foundPatients.isEmpty()){
            throw new CustomErrorException(ErrorType.UserNotFoundError);
        }

        return foundPatients;
    }
}
