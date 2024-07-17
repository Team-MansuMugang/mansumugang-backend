package org.mansumugang.mansumugang_service.service.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.mansumugang.mansumugang_service.constant.ErrorType;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.domain.user.UserLocation;
import org.mansumugang.mansumugang_service.dto.user.location.PatientLocationDto;
import org.mansumugang.mansumugang_service.dto.user.location.PatientLocationRequestDto;
import org.mansumugang.mansumugang_service.exception.CustomErrorException;
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

    @Transactional
    public PatientLocationDto saveUserLocation(User user, PatientLocationRequestDto patientLocationRequestDto ){
        log.info("saveUserLocation 호출");

        // 1. 요청으로 온 userId로 찾은 유저가 존재하는지 검증
        User findUser = validateUser(user.getId());

        // 2. 경도, 위도 유효성 검사(대한민국 안에 존재하는지 체크)
        validateUserLocation(patientLocationRequestDto);

        // 3. 경위도 정보 저장
        UserLocation savedUserLocation = userLocationRepository.save(UserLocation.fromRequestDto(findUser, patientLocationRequestDto));
        log.info("경위도 정보 저장 완료");


        return PatientLocationDto.fromEntity(findUser,savedUserLocation);
    }

    public PatientLocationDto getUserLocation(Long userId){

        // 1. 요청으로 온 userId로 찾은 유저가 존재하는지 검증
        User findUser = validateUser(userId);

        // 2. user_id로 찾아진 유저 마지막 위치 저장 시간순으로 내림차순 후 하나의 튜플 추출
        UserLocation foundedLocationInfo = userLocationRepository.findTopByUserOrderByCreatedAtDesc(findUser)
                .orElseThrow(()-> new CustomErrorException(ErrorType.UserLocationInfoNotFoundError));

        UserLocation userLocation = new UserLocation(foundedLocationInfo.getLatitude(), foundedLocationInfo.getLongitude(), foundedLocationInfo.getCreatedAt(), findUser);

        return PatientLocationDto.fromEntity(findUser, userLocation);


    }



    public User validateUser(Long userId) {
        log.info("userId로 사용자 찾기 시작, userId={}", userId);

        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("userId로 찾은 유저가 존재하지 않음, userId={}", userId);
                    return new CustomErrorException(ErrorType.UserNotFoundError);
                });
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