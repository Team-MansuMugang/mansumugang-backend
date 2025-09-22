package org.mansumugang.mansumugang_service.service.location;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.mansumugang.mansumugang_service.constant.ErrorType;
import org.mansumugang.mansumugang_service.domain.user.Patient;
import org.mansumugang.mansumugang_service.domain.user.Protector;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.domain.location.Location;
import org.mansumugang.mansumugang_service.dto.location.PatientLocation;
import org.mansumugang.mansumugang_service.dto.location.PatientLocationList;
import org.mansumugang.mansumugang_service.dto.location.PatientLocationRequestDto;
import org.mansumugang.mansumugang_service.exception.CustomErrorException;
import org.mansumugang.mansumugang_service.repository.PatientRepository;
import org.mansumugang.mansumugang_service.repository.UserLocationRepository;
import org.mansumugang.mansumugang_service.service.user.UserCommonService;
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

    private final UserCommonService userCommonService;

    @Transactional
    public PatientLocation.Dto saveUserLocation(User user, PatientLocationRequestDto patientLocationRequestDto ){

        Patient validPatient = userCommonService.findPatient(user);

        validateUserLocation(patientLocationRequestDto);

        Location savedLocation = userLocationRepository.save(Location.fromRequestDto(validPatient, patientLocationRequestDto));

        return PatientLocation.Dto.of(validPatient, savedLocation);
    }

    public PatientLocation.Dto getUserLatestLocation(User user, Long patientId){

        Protector validProtector = userCommonService.findProtector(user);
        Patient foundPatient = userCommonService.findPatient(patientId);

        userCommonService.checkUserIsProtectorOfPatient(validProtector, foundPatient);

        Location foundedLatestLocationInfo = getUserLatestLocation(foundPatient);

        Location location = new Location(foundedLatestLocationInfo.getLatitude(), foundedLatestLocationInfo.getLongitude(), foundedLatestLocationInfo.getCreatedAt(), foundPatient);

        return PatientLocation.Dto.of(foundPatient, location);


    }

    public PatientLocationList.Dto getUserLocationWithinRange(User user, Long patientId, LocalDateTime standardTime){

        LocalDateTime inquiryStartTime = standardTime.minusMinutes(30);
        LocalDateTime inquiryEndTime = standardTime.plusMinutes(30);

        Protector validProtector = userCommonService.findProtector(user);
        Patient foundPatient = userCommonService.findPatient(patientId);

        userCommonService.checkUserIsProtectorOfPatient(validProtector, foundPatient);

        List<Location> foundedLocationInfoWithinRange = getUserLocationsWithinRange(foundPatient, inquiryStartTime, inquiryEndTime);

        if(foundedLocationInfoWithinRange.isEmpty()){
            throw new CustomErrorException(ErrorType.UserLocationInfoWithinRangeNotFoundError);
        }

        List<PatientLocationList.LocationElement> locations = foundedLocationInfoWithinRange.stream()
                .map(userLocation -> PatientLocationList.LocationElement.fromEntity(userLocation))
                .collect(Collectors.toList());

        return PatientLocationList.Dto.of(foundPatient, locations);
    }

    public void validateUserLocation(PatientLocationRequestDto patientLocationRequestDto) {

        if (!(EXTREME_SOUTH.getCoordinate() < patientLocationRequestDto.getLatitude() && patientLocationRequestDto.getLatitude() < EXTREME_NORTH.getCoordinate())
                || !(EXTREME_WEST.getCoordinate() < patientLocationRequestDto.getLongitude() && patientLocationRequestDto.getLongitude() < EXTREME_EAST.getCoordinate())){

            throw new CustomErrorException(ErrorType.OutOfBoundaryError);
        }

    }

    public Location getUserLatestLocation(Patient foundPatient) {
        return userLocationRepository.findTopByPatientOrderByCreatedAtDesc(foundPatient)
                .orElseThrow(()-> new CustomErrorException(ErrorType.UserLocationInfoNotFoundError));
    }

    public List<Location> getUserLocationsWithinRange(Patient foundPatient, LocalDateTime inquiryStartTime, LocalDateTime inquiryEndTime) {
        return userLocationRepository.findByPatientWithinTimeRange(foundPatient, inquiryStartTime, inquiryEndTime);
    }

}