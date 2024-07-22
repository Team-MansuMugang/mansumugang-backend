package org.mansumugang.mansumugang_service.service.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.mansumugang.mansumugang_service.constant.ErrorType;
import org.mansumugang.mansumugang_service.domain.user.Patient;
import org.mansumugang.mansumugang_service.domain.user.Protector;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.domain.user.UserLocation;
import org.mansumugang.mansumugang_service.dto.user.location.PatientLocationDto;
import org.mansumugang.mansumugang_service.dto.user.location.PatientLocationRequestDto;
import org.mansumugang.mansumugang_service.exception.CustomErrorException;
import org.mansumugang.mansumugang_service.repository.PatientRepository;
import org.mansumugang.mansumugang_service.repository.ProtectorRepository;
import org.mansumugang.mansumugang_service.repository.UserLocationRepository;
import org.mansumugang.mansumugang_service.repository.UserRepository;
import org.springframework.stereotype.Service;

import static org.mansumugang.mansumugang_service.constant.LocationBoundary.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserLocationService {

    private final UserLocationRepository userLocationRepository;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final ProtectorRepository protectorRepository;

    @Transactional
    public PatientLocationDto saveUserLocation(User patient, PatientLocationRequestDto patientLocationRequestDto ){
        log.info("saveUserLocation 호출");

        // 1. 경도, 위도 유효성 검사(대한민국 안에 존재하는지 체크)
        validateUserLocation(patientLocationRequestDto);

        // 2. 경위도 정보 저장
        UserLocation savedUserLocation = userLocationRepository.save(UserLocation.fromRequestDto((Patient) patient, patientLocationRequestDto));
        log.info("경위도 정보 저장 완료");


        return PatientLocationDto.fromEntity((Patient) patient,savedUserLocation);
    }

    public PatientLocationDto getUserLocation(User protector, Long patientId){

        // 1. 요청으로 온 userId로 찾은 유저가 존재하는지 검증
        log.info("환자, 보호자 유효성 검사 실시");

        Patient foundPatient = validatePatient(patientId);
        validateProtector(protector); // protector 가 null 인지 검증

        log.info("환자, 보호자 유효성 검사 완료. 환자 고유번호(user_id)={}, 보호자 고유번호(user_id)={}", foundPatient.getId(), protector.getId());

        // 2. 요청으로 온 보호자의 엑세스 토큰으로 보호자의 환자 조회
        log.info("보호자와 환자 간 관계 유효성 검사 시작");
        checkUserIsProtectorOfPatient((Protector) protector, foundPatient);


        // 3. user_id로 찾아진 유저 마지막 위치 저장 시간순으로 내림차순 후 하나의 튜플 추출
        UserLocation foundedLocationInfo = userLocationRepository.findTopByPatientOrderByCreatedAtDesc(foundPatient)
                .orElseThrow(()-> new CustomErrorException(ErrorType.UserLocationInfoNotFoundError));

        UserLocation userLocation = new UserLocation(foundedLocationInfo.getLatitude(), foundedLocationInfo.getLongitude(), foundedLocationInfo.getCreatedAt(), foundPatient);

        return PatientLocationDto.fromEntity(foundPatient, userLocation);


    }

    public Patient validatePatient(Long patientId) {
        log.info("patientId로 환자 찾기 시작, patientId={}", patientId);

        return patientRepository.findById(patientId)
                .orElseThrow(() -> {
                    log.error("userId로 찾은 환자가 존재하지 않음, userId={}", patientId);
                    return new CustomErrorException(ErrorType.UserNotFoundError);
                });
    }

    public void validateProtector(User protector) {
        if (protector == null) {
            throw new CustomErrorException(ErrorType.UserNotFoundError);
        }
    }

    private void checkUserIsProtectorOfPatient(Protector protector, Patient patient) {

        log.info("유저가 환자의 보호자인지 검증 시작");
        if(!patient.getProtector().getUsername().equals(protector.getUsername())) {
            throw new CustomErrorException(ErrorType.AccessDeniedError);
        }
        log.info("유저가 환자의 보호자인지 검증 완료");
    }


    public void validateUserLocation(PatientLocationRequestDto patientLocationRequestDto) {

        log.info("경위도 유효성 검사 시작");
        log.info("요청 받은 경도, longitude={}", patientLocationRequestDto.getLongitude());
        log.info("요청 받은 위도, latitude={}", patientLocationRequestDto.getLatitude());

        if (!(EXTREME_SOUTH.getCoordinate() < patientLocationRequestDto.getLatitude() && patientLocationRequestDto.getLatitude() < EXTREME_NORTH.getCoordinate())
                || !(EXTREME_WEST.getCoordinate() < patientLocationRequestDto.getLongitude() && patientLocationRequestDto.getLongitude() < EXTREME_EAST.getCoordinate())){

            throw new CustomErrorException(ErrorType.OutOfBoundaryError);
        }

        log.info("경위도 유효성 검사 완료");
    }

}