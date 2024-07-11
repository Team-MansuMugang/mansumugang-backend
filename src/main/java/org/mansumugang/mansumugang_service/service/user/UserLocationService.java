package org.mansumugang.mansumugang_service.service.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.mansumugang.mansumugang_service.constant.ErrorType;
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
    public PatientLocationDto saveUserLocation(Long userId, PatientLocationRequestDto patientLocationRequestDto ){
        log.info("saveUserLocation 호출");

        // 1. 요청으로 온 userId로 찾은 유저가 존재하는지 검증
        validateUser(userId);

        // 2. 경도, 위도 유효성 검사(대한민국 안에 존재하는지 체크)
        validateUserLocation(patientLocationRequestDto);

        // 3. 경위도 정보 저장
        UserLocation savedUserLocation = userLocationRepository.save(UserLocation.fromRequestDto(userId, patientLocationRequestDto));
        log.info("경위도 정보 저장 완료");

        return PatientLocationDto.fromEntity(savedUserLocation);
    }



    public void validateUser(Long userId) {
        log.info("userId로 사용자 찾기 시작");

        if (userRepository.findById(userId).isEmpty()){
            throw new CustomErrorException(ErrorType.UserNotFoundError);
        }
        log.info("userId로 찾은 유저에 대한 유효성 검사 완료");

    }


    public void validateUserLocation(PatientLocationRequestDto patientLocationRequestDto) {

        log.info("경위도 유효성 검사 시작");

        if (!(EXTREME_SOUTH.getCoordinate() < patientLocationRequestDto.getLongitude() && patientLocationRequestDto.getLongitude() < EXTREME_NORTH.getCoordinate())
        || !(EXTREME_WEST.getCoordinate() < patientLocationRequestDto.getLatitude() && patientLocationRequestDto.getLatitude() < EXTREME_EAST.getCoordinate())){

            throw new CustomErrorException(ErrorType.NotValidRequestError);
        }

        log.info("경위도 유효성 검사 완료");
    }

}
