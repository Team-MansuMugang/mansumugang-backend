package org.mansumugang.mansumugang_service.service.location;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.mansumugang.mansumugang_service.constant.ErrorType;
import org.mansumugang.mansumugang_service.domain.user.Patient;
import org.mansumugang.mansumugang_service.domain.user.Protector;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.domain.location.Location;
import org.mansumugang.mansumugang_service.dto.location.PatientLocationDto;
import org.mansumugang.mansumugang_service.dto.location.PatientLocationRequestDto;
import org.mansumugang.mansumugang_service.dto.location.PatientLocationResponseDto;
import org.mansumugang.mansumugang_service.exception.CustomErrorException;
import org.mansumugang.mansumugang_service.repository.PatientRepository;
import org.mansumugang.mansumugang_service.repository.UserLocationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mansumugang.mansumugang_service.constant.LocationBoundary.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserLocationService {

    private final UserLocationRepository userLocationRepository;
    private final PatientRepository patientRepository;

    @Transactional
    public PatientLocationDto saveUserLocation(User patient, PatientLocationRequestDto patientLocationRequestDto ){
        log.info("saveUserLocation 호출");
        // 1. @AuthenticationPrincipal 로 받은 User 객체가 null 인지 검증
        validateUser(patient);

        // 2. 경도, 위도 유효성 검사(대한민국 안에 존재하는지 체크)
        validateUserLocation(patientLocationRequestDto);

        // 3. 경위도 정보 저장
        Location savedLocation = userLocationRepository.save(Location.fromRequestDto((Patient) patient, patientLocationRequestDto));
        log.info("경위도 정보 저장 완료");


        return PatientLocationDto.fromEntity((Patient) patient, savedLocation);
    }

    public PatientLocationDto getUserLatestLocation(User protector, Long patientId){

        // 1. 요청으로 온 userId로 찾은 유저가 존재하는지 검증
        log.info("환자, 보호자 유효성 검사 실시");

        Patient foundPatient = validatePatient(patientId);
        validateUser(protector); // protector 가 null 인지 검증

        log.info("환자, 보호자 유효성 검사 완료. 환자 고유번호(user_id)={}, 보호자 고유번호(user_id)={}", foundPatient.getId(), protector.getId());

        // 2. 요청으로 온 보호자의 엑세스 토큰으로 보호자의 환자 조회
        log.info("보호자와 환자 간 관계 유효성 검사 시작");
        checkUserIsProtectorOfPatient((Protector) protector, foundPatient);


        // 3. user_id로 찾아진 유저 마지막 위치 저장 시간순으로 내림차순 후 하나의 튜플 추출
        Location foundedLatestLocationInfo = getUserLatestLocation(foundPatient);

        Location location = new Location(foundedLatestLocationInfo.getLatitude(), foundedLatestLocationInfo.getLongitude(), foundedLatestLocationInfo.getCreatedAt(), foundPatient);

        return PatientLocationDto.fromEntity(foundPatient, location);


    }

    public List<PatientLocationResponseDto> getUserLocationWithinRange(User protector, Long patientId, LocalDateTime standardTime){
        // 0. 호출 시간 확인
        log.info("쿼리 파라미터로 받은 시간 : {}", standardTime);

        LocalDateTime inquiryStartTime = standardTime.minusMinutes(30);
        LocalDateTime inquiryEndTime = standardTime.plusMinutes(30);

        log.info("환자 위치 조회 요청 시간 범위={} ~ {}" , inquiryStartTime, inquiryEndTime);

        // 1. 요청으로 온 userId로 찾은 유저가 존재하는지 검증
        log.info("환자, 보호자 유효성 검사 실시");

        Patient foundPatient = validatePatient(patientId);
        validateUser(protector); // protector 가 null 인지 검증

        log.info("환자, 보호자 유효성 검사 완료. 환자 고유번호(user_id)={}, 보호자 고유번호(user_id)={}", foundPatient.getId(), protector.getId());

        // 2. 요청으로 온 보호자의 엑세스 토큰으로 보호자의 환자 조회
        log.info("보호자와 환자 간 관계 유효성 검사 시작");
        checkUserIsProtectorOfPatient((Protector) protector, foundPatient);

        // 3. user_id로 찾아진 유저의 위치 저장 시간순으로 내림차순 후 전체 튜플 추출(범위 : 요청 시간 플러스 마이너스 30분)
        List<Location> foundedLocationInfoWithinRange = getUserLocationsWithinRange(foundPatient, inquiryStartTime, inquiryEndTime);
        log.info("요청 시간 전후 30분 동안 조회된 환자 위치 개수={}", (long) foundedLocationInfoWithinRange.size());

        List<PatientLocationDto> patientLocationDtos = foundedLocationInfoWithinRange.stream()
                .map(userLocation -> PatientLocationDto.fromEntity(foundPatient, userLocation))
                .collect(Collectors.toList());

        return PatientLocationResponseDto.fromDtoList(foundPatient, patientLocationDtos);
    }


    public void validateUser(User user) {
        log.info("@AuthenticationPrincipal로 받은 User 객체가 null 인지 확인 시작");
        if (user == null) {
            throw new CustomErrorException(ErrorType.UserNotFoundError);
        }

        log.info("@AuthenticationPrincipal로 받은 User 객체가 null 인지 확인 완료");
    }

    public Patient validatePatient(Long patientId) {
        log.info("patientId로 환자 찾기 시작, patientId={}", patientId);

        return patientRepository.findById(patientId)
                .orElseThrow(() -> {
                    log.error("userId로 찾은 환자가 존재하지 않음, userId={}", patientId);
                    return new CustomErrorException(ErrorType.UserNotFoundError);
                });
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

    public Location getUserLatestLocation(Patient foundPatient) {
        return userLocationRepository.findTopByPatientOrderByCreatedAtDesc(foundPatient)
                .orElseThrow(()-> new CustomErrorException(ErrorType.UserLocationInfoNotFoundError));
    }

    public List<Location> getUserLocationsWithinRange(Patient foundPatient, LocalDateTime inquiryStartTime, LocalDateTime inquiryEndTime) {
        return Optional.ofNullable(userLocationRepository.findByPatientWithinTimeRange(foundPatient, inquiryStartTime, inquiryEndTime))
                .orElseThrow(() -> new CustomErrorException(ErrorType.UserLocationInfoNotFoundError));
    }

}