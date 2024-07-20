package org.mansumugang.mansumugang_service.service.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.mansumugang.mansumugang_service.constant.ErrorType;
import org.mansumugang.mansumugang_service.domain.user.Patient;
import org.mansumugang.mansumugang_service.domain.user.Protector;
import org.mansumugang.mansumugang_service.domain.user.UserLocation;
import org.mansumugang.mansumugang_service.dto.user.location.PatientLocationDto;
import org.mansumugang.mansumugang_service.dto.user.location.PatientLocationInquiryRequestDto;
import org.mansumugang.mansumugang_service.dto.user.location.PatientLocationRequestDto;
import org.mansumugang.mansumugang_service.exception.CustomErrorException;
import org.mansumugang.mansumugang_service.repository.PatientRepository;
import org.mansumugang.mansumugang_service.repository.ProtectorRepository;
import org.mansumugang.mansumugang_service.repository.UserLocationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.mansumugang.mansumugang_service.constant.LocationBoundary.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserLocationService {

    private final UserLocationRepository userLocationRepository;
    private final PatientRepository patientRepository;
    private final ProtectorRepository protectorRepository;

    @Transactional
    public PatientLocationDto saveUserLocation(Patient patient, PatientLocationRequestDto patientLocationRequestDto ){
        log.info("saveUserLocation 호출");

        // 1. 요청으로 온 userId로 찾은 유저가 존재하는지 검증
        Patient foundPatient = validatePatient(patient.getId());

        // 2. 경도, 위도 유효성 검사(대한민국 안에 존재하는지 체크)
        validateUserLocation(patientLocationRequestDto);

        // 3. 경위도 정보 저장
        UserLocation savedUserLocation = userLocationRepository.save(UserLocation.fromRequestDto(foundPatient, patientLocationRequestDto));
        log.info("경위도 정보 저장 완료");


        return PatientLocationDto.fromEntity(foundPatient,savedUserLocation);
    }

    public PatientLocationDto getUserLocation(Protector protector, PatientLocationInquiryRequestDto patientLocationInquiryRequestDto){

        // 1. 요청으로 온 userId로 찾은 유저가 존재하는지 검증
        log.info("환자, 보호자 유효성 검사 실시");

        Patient foundPatient = validatePatient(patientLocationInquiryRequestDto.getPatientId());
        Protector foundProtector = validateProtector(protector.getId()); // 현재 로그인 되있는 보호자의 정보를 검증하는게 의미가 있나(?)

        log.info("환자, 보호자 유효성 검사 완료. 환자 고유번호(user_id)={}, 보호자 고유번호(user_id)={}", foundPatient.getId(), foundProtector.getId());

        // 2. 요청으로 온 보호자의 엑세스 토큰으로 보호자의 환자 조회
        log.info("보호자와 환자 간 관계 유효성 검사 시작");
        validateRelation(foundProtector, foundPatient);


        // 3. user_id로 찾아진 유저 마지막 위치 저장 시간순으로 내림차순 후 하나의 튜플 추출
        UserLocation foundedLocationInfo = userLocationRepository.findTopByPatientOrderByCreatedAtDesc(foundPatient)
                .orElseThrow(()-> new CustomErrorException(ErrorType.UserLocationInfoNotFoundError));

        UserLocation userLocation = new UserLocation(foundedLocationInfo.getLatitude(), foundedLocationInfo.getLongitude(), foundedLocationInfo.getCreatedAt(), foundPatient);

        return PatientLocationDto.fromEntity(foundPatient, userLocation);


    }

    public Patient validatePatient(Long userId) {
        log.info("userId로 환자 찾기 시작, userId={}", userId);

        return patientRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("userId로 찾은 환자가 존재하지 않음, userId={}", userId);
                    return new CustomErrorException(ErrorType.UserNotFoundError);
                });
    }

    public Protector validateProtector(Long userId) {
        log.info("userId로 보호자 찾기 시작, userId={}", userId);

        return protectorRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("userId로 찾은 보호자가 존재하지 않음, userId={}", userId);
                    return new CustomErrorException(ErrorType.UserNotFoundError);
                });
    }

    private void validateRelation(Protector foundProtector, Patient foundPatient) {

        log.info("보호자의 고유번호(user_id)로 환자 보호자의 환자 명단 조회 시작");
        List<Patient> patientList = patientRepository.findByProtector_id(foundProtector.getId());
        log.info("보호자의 환자 명단 조회 결과 ={}명", (long) patientList.size());

        // 보호자가 관리하는 환자 목록에 요청한 환자가 포함되어 있는지 검증
        log.info("환자 보호자 관계 검증 시작.");
        boolean isPatientManagedByProtector = patientList.stream()
                .anyMatch(patient -> patient.getId().equals(foundPatient.getId()));

        if (!isPatientManagedByProtector) {
            throw new CustomErrorException(ErrorType.RelationDismatchError);  // 보호자가 해당 환자를 관리하지 않는 경우
        }

        log.info("환자 보호자 관계 검증 완료.");
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