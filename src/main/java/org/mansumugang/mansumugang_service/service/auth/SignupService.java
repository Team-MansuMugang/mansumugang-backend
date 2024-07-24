package org.mansumugang.mansumugang_service.service.auth;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mansumugang.mansumugang_service.constant.ErrorType;
import org.mansumugang.mansumugang_service.domain.user.Patient;
import org.mansumugang.mansumugang_service.domain.user.Protector;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.dto.auth.signup.*;
import org.mansumugang.mansumugang_service.exception.CustomErrorException;
import org.mansumugang.mansumugang_service.repository.PatientRepository;
import org.mansumugang.mansumugang_service.repository.ProtectorRepository;
import org.mansumugang.mansumugang_service.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SignupService {

    private final UserRepository userRepository;
    private final ProtectorRepository protectorRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SignUpDto patientSignup(PatientSignupRequestDto patientSignupRequestDto){

        log.info("환자 아이디 중복 체크");
        if(userRepository.findByUsername(patientSignupRequestDto.getUsername()).isPresent()){ // 중복된 아이디가 있을 경우 예외
            throw new CustomErrorException(ErrorType.DuplicatedUsernameError);
        }
        log.info("환자 아이디 중복 체크 완료");

        // 비밀번호, 비밀번호 재입력이 동일한지 검사
        log.info("환자 비밀번호, 비밀번호 재확인 체크 시작");
        checkPasswordIsEqual(patientSignupRequestDto.getPassword(), patientSignupRequestDto.getPasswordCheck());
        log.info("환자 비밀번호, 비밀번호 재확인 체크 완료");

        log.info("환자의 보호자 아이디로 고유번호 추출 시작");
        Protector foundProtector = findProtector(patientSignupRequestDto);
        log.info("보호자 고유번호 추출완료, 입력한 보호자 아이디={}, 찾은 보호자 아이디={}, 찾은 보호자 고유번호={}", patientSignupRequestDto.getProtectorUsername(), foundProtector.getUsername(), foundProtector.getId());


        // 성공로직(아이디,닉네임 중복X 및 비밀번호(1차입력,2차입력)동일시)
        log.info("환자 정보 DB 저장 시작");
        Patient newPatientUser = userRepository.save(Patient.patientRequestDtoToUser(foundProtector,patientSignupRequestDto, passwordEncoder));
        log.info("환자 정보 DB 저장 완료");

        return SignUpDto.fromPatientEntity(newPatientUser);

    }


    @Transactional
    public SignUpDto protectorSignup(ProtectorSignUpRequestDto protectorSignUpRequestDto){
        if(userRepository.findByUsername(protectorSignUpRequestDto.getUsername()).isPresent()){ // 중복된 아이디가 있을 경우 예외
            throw new CustomErrorException(ErrorType.DuplicatedUsernameError);
        }

        if(protectorRepository.findByNickname(protectorSignUpRequestDto.getNickname()).isPresent()){
            throw new CustomErrorException(ErrorType.DuplicatedNicknameError);
        }

        // 비밀번호, 비밀번호 재입력이 동일한지 검사
        checkPasswordIsEqual(protectorSignUpRequestDto.getPassword(), protectorSignUpRequestDto.getPasswordCheck());

        // 성공로직(아이디,닉네임 중복X 및 비밀번호(1차입력,2차입력)동일시)
        Protector newProtectorUser = userRepository.save(Protector.protectorRequestDtoToUser(protectorSignUpRequestDto, passwordEncoder));
        return SignUpDto.fromProtectorEntity(newProtectorUser);

    }

    //받은 protectorUsername 으로 해당 보호자 존재 유/무 확인 후 존재한다면 해당 보호자 객체 반환
    private Protector findProtector(PatientSignupRequestDto patientSignupRequestDto) {
        return (Protector) userRepository.findByUsername(patientSignupRequestDto.getProtectorUsername())
                .orElseThrow(() -> new CustomErrorException(ErrorType.UserNotFoundError));
    }


    public void checkPasswordIsEqual(String password, String passwordCheck){
        if(!password.equals(passwordCheck)){
            throw new CustomErrorException(ErrorType.NotEqualPasswordAndPasswordCheck);//에러코드
        }
    }

    public void checkUsernameDuplication(UsernameDuplicationCheckDto usernameDuplicationCheckDto){

        // 레퍼지토리에 같은 아이디가 존재 -> 에러발생
        if(userRepository.findByUsername(usernameDuplicationCheckDto.getUsername()).isPresent()){
            throw new CustomErrorException(ErrorType.DuplicatedUsernameError);

        }
    }

    public void checkProtectorUsername(ProtectorUsernameCheckRequestDto protectorUsernameCheckRequestDto){

        // 1. userRepository 에서 requestDto 로 받은 아이디로 해당 유저 존재하는지 확인 없으면 에러 발생.
        Optional<User> foundUser = userRepository.findByUsername(protectorUsernameCheckRequestDto.getUsername());

        if (foundUser.isEmpty()){
            throw new CustomErrorException(ErrorType.UserNotFoundError);
        }

        // 2. 찾은 유저의 userType 이 User_protector 라면 패스, User_patient 라면 오류 발생.
        if (Objects.equals(foundUser.get().getUsertype(), "User_patient")){
            throw new CustomErrorException(ErrorType.UserTypeDismatchError);
        }

    }

    public void checkNicknameDuplication(NicknameDuplicationCheckDto nicknameDuplicationCheckDto){
        if(protectorRepository.findByNickname(nicknameDuplicationCheckDto.getNickname()).isPresent()){
            throw new CustomErrorException(ErrorType.DuplicatedNicknameError);
        }
    }

}
