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

        if(userRepository.findByUsername(patientSignupRequestDto.getUsername()).isPresent()){
            throw new CustomErrorException(ErrorType.DuplicatedUsernameError);
        }

        checkPasswordIsEqual(patientSignupRequestDto.getPassword(), patientSignupRequestDto.getPasswordCheck());

        Protector foundProtector = findProtector(patientSignupRequestDto);

        Patient newPatientUser = userRepository.save(Patient.patientRequestDtoToUser(foundProtector,patientSignupRequestDto, passwordEncoder));

        return SignUpDto.fromPatientEntity(newPatientUser);

    }


    @Transactional
    public SignUpDto protectorSignup(ProtectorSignUpRequestDto protectorSignUpRequestDto){
        if(userRepository.findByUsername(protectorSignUpRequestDto.getUsername()).isPresent()){
            throw new CustomErrorException(ErrorType.DuplicatedUsernameError);
        }

        if(protectorRepository.findByNickname(protectorSignUpRequestDto.getNickname()).isPresent()){
            throw new CustomErrorException(ErrorType.DuplicatedNicknameError);
        }

        checkPasswordIsEqual(protectorSignUpRequestDto.getPassword(), protectorSignUpRequestDto.getPasswordCheck());

        Protector newProtectorUser = userRepository.save(Protector.protectorRequestDtoToUser(protectorSignUpRequestDto, passwordEncoder));
        return SignUpDto.fromProtectorEntity(newProtectorUser);

    }

    private Protector findProtector(PatientSignupRequestDto patientSignupRequestDto) {

        User foundUser = userRepository.findByUsername(patientSignupRequestDto.getProtectorUsername())
                .orElseThrow(() -> new CustomErrorException(ErrorType.UserNotFoundForSignupError));

        if (!foundUser.getUsertype().equals("USER_PROTECTOR")){
            throw new CustomErrorException(ErrorType.UserTypeDismatchForSignupError);
        }

        return (Protector) foundUser;
    }


    public void checkPasswordIsEqual(String password, String passwordCheck){
        if(!password.equals(passwordCheck)){
            throw new CustomErrorException(ErrorType.NotEqualPasswordAndPasswordCheck);//에러코드
        }
    }

    public void checkUsernameDuplication(UsernameDuplicationCheckDto usernameDuplicationCheckDto){

        if(userRepository.findByUsername(usernameDuplicationCheckDto.getUsername()).isPresent()){
            throw new CustomErrorException(ErrorType.DuplicatedUsernameError);

        }
    }

    public void checkProtectorUsername(ProtectorUsernameCheckRequestDto protectorUsernameCheckRequestDto){

        User foundUser = userRepository.findByUsername(protectorUsernameCheckRequestDto.getUsername())
                .orElseThrow(() -> new CustomErrorException(ErrorType.UserNotFoundError));

        if (foundUser.getUsertype().equals("USER_PATIENT")){
            throw new CustomErrorException(ErrorType.UserTypeDismatchError);
        }

    }

    public void checkNicknameDuplication(NicknameDuplicationCheckDto nicknameDuplicationCheckDto){
        if(protectorRepository.findByNickname(nicknameDuplicationCheckDto.getNickname()).isPresent()){
            throw new CustomErrorException(ErrorType.DuplicatedNicknameError);
        }
    }

}
