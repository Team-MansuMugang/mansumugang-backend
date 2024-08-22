package org.mansumugang.mansumugang_service.service.user;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mansumugang.mansumugang_service.constant.ErrorType;
import org.mansumugang.mansumugang_service.domain.user.Patient;
import org.mansumugang.mansumugang_service.domain.user.Protector;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.dto.user.*;
import org.mansumugang.mansumugang_service.exception.CustomErrorException;
import org.mansumugang.mansumugang_service.repository.PatientRepository;
import org.mansumugang.mansumugang_service.repository.ProtectorRepository;
import org.mansumugang.mansumugang_service.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final ProtectorRepository protectorRepository;

    public PatientInquiry.Dto getPatientsByProtector(User user){

        // 1. AuthenticationPrincipal 로 넘겨받은 user 가 보호자 객체인지 검증
        Protector validProtector = validateProtector(user);

        // 2. 검증된 보호자 객체로 보호자의 환자 찾기.
        List<Patient> foundPatients = getAllPatientsByProtectorId(validProtector);

        return PatientInquiry.Dto.fromEntity(foundPatients);

    }

    public ProtectorInfoInquiry.Dto getProtectorOwnInfo(User user){

        // 1. AuthenticationPrincipal 로 넘겨받은 user 가 보호자 객체인지 검증
        Protector validProtector = validateProtector(user);

        return ProtectorInfoInquiry.Dto.fromEntity(validProtector);

    }

    public PatientInfoInquiry.Dto getPatientOwnInfo(User user){

        // 1. AuthenticationPrincipal 로 넘겨받은 user 가 보호자 객체인지 검증
        Patient validPatient = validatePatient(user);

        return PatientInfoInquiry.Dto.fromEntity(validPatient);

    }

    @Transactional
    public ProtectorInfoUpdate.Dto updateProtectorInfo(User user, Long protectorId ,ProtectorInfoUpdate.Request request){

        // 1.User 가 보호자 객체인지 검증
        Protector validProtector = validateProtector(user);

        Protector foundProtector = protectorRepository.findById(protectorId)
                .orElseThrow(() -> new CustomErrorException(ErrorType.UserNotFoundError));

        // 2. 검증된 보호자 객체의 id와 경로변수의 protectorId가 동일한지 검증. => 아니면 예외 처리
        if (!validProtector.getId().equals(foundProtector.getId())){
            throw new CustomErrorException(ErrorType.AccessDeniedError);
        }

        // 3. 변경하려는 닉네임이 기존 본인의 닉네임과 동일하지 않거나 다른 닉네임이 존재한다면 => 예외 처리.

        // 닉네임 변경 안함, 닉네임 변경함.
        // 변경 안함 ->
        String newNickname = request.getNickname();

        if (!validProtector.getNickname().equals(newNickname)){
            if(protectorRepository.findByNickname(newNickname).isPresent()){
                throw new CustomErrorException(ErrorType.DuplicatedNicknameError);
            }
        }

        // 4. 유저 정보 업데이트 시작.
        foundProtector.update(request, newNickname);

        return ProtectorInfoUpdate.Dto.fromEntity(validProtector);

    }

    @Transactional
    public PatientInfoUpdate.Dto updatePatientInfo(User user, Long patientId ,PatientInfoUpdate.Request request){

        // 1. User 가 환자 객체인지 검증
        Patient validPatient = validatePatient(user);

        // 2. 경로변수로 받은 환자 객체 조회
        Patient foundPatient = patientRepository.findById(patientId)
                .orElseThrow(() -> new CustomErrorException(ErrorType.UserNotFoundError));

        // 3. validPatent와 foundPatent가 동일한 환자이어야함.
        if (!validPatient.getId().equals(foundPatient.getId())){
            throw new CustomErrorException(ErrorType.AccessDeniedError);
        }

        // 4. 유저 정보 업데이트 시작.
        foundPatient.update(request);

        return PatientInfoUpdate.Dto.fromEntity(validPatient);

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

    private Patient validatePatient(User user){
        if (user == null){
            throw new CustomErrorException(ErrorType.UserNotFoundError);
        }

        if(user instanceof Patient){
            return (Patient) user;
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
