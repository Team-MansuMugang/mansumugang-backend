package org.mansumugang.mansumugang_service.service.hospital;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mansumugang.mansumugang_service.constant.ErrorType;
import org.mansumugang.mansumugang_service.domain.hospital.Hospital;
import org.mansumugang.mansumugang_service.domain.user.Patient;
import org.mansumugang.mansumugang_service.domain.user.Protector;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.dto.hospital.HospitalDetailGet;
import org.mansumugang.mansumugang_service.dto.hospital.HospitalSave;
import org.mansumugang.mansumugang_service.dto.hospital.HospitalUpdate;
import org.mansumugang.mansumugang_service.exception.CustomErrorException;
import org.mansumugang.mansumugang_service.repository.HospitalRepository;
import org.mansumugang.mansumugang_service.service.user.UserCommonService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static org.mansumugang.mansumugang_service.constant.LocationBoundary.*;
import static org.mansumugang.mansumugang_service.constant.LocationBoundary.EXTREME_EAST;

@Service
@RequiredArgsConstructor
@Transactional
public class HospitalService {
    private final HospitalRepository hospitalRepository;
    private final UserCommonService userCommonService;

    public HospitalDetailGet.Dto getHospitalDetail(User user, Long hospitalId) {
        Hospital foundHospital = findHospital(hospitalId);

        Protector validProtector = userCommonService.findProtector(user);
        Patient foundPatient = foundHospital.getPatient();
        userCommonService.checkUserIsProtectorOfPatient(validProtector, foundPatient);

        return HospitalDetailGet.Dto.fromEntity(foundHospital);
    }

    public void saveHospital(User user, HospitalSave.Request requestDto) {
        Protector validProtector = userCommonService.findProtector(user);
        Patient foundPatient = userCommonService.findPatient(requestDto.getPatientId());
        userCommonService.checkUserIsProtectorOfPatient(validProtector, foundPatient);

        LocalDateTime filteredLocalDateTime = validateHospitalVisitingTime(foundPatient, requestDto.getHospitalVisitingTime());

        hospitalRepository.save(Hospital.of(requestDto, foundPatient, filteredLocalDateTime));
    }

    public void updateHospital(User user, Long hospitalId, HospitalUpdate.Request requestDto) {
        Hospital foundHospital = findHospital(hospitalId);

        Protector validProtector = userCommonService.findProtector(user);
        Patient foundPatient = foundHospital.getPatient();
        userCommonService.checkUserIsProtectorOfPatient(validProtector, foundPatient);

        if (requestDto.getHospitalName() != null) {
            foundHospital.setHospitalName(requestDto.getHospitalName());
        }

        if (requestDto.getHospitalAddress() != null) {
            if (requestDto.getLatitude() == null || requestDto.getLongitude() == null) {
                throw new CustomErrorException(ErrorType.NeedLatitudeAndLongitudeError);
            }

            foundHospital.setHospitalAddress(requestDto.getHospitalAddress());
            foundHospital.setLatitude(requestDto.getLatitude());
            foundHospital.setLongitude(requestDto.getLongitude());
        }

        if (requestDto.getHospitalDescription() != null) {
            foundHospital.setHospitalDescription(requestDto.getHospitalDescription());
        }

        if (requestDto.getHospitalVisitingTime() != null) {
            LocalDateTime filteredLocalDateTime = validateHospitalVisitingTime(foundPatient, requestDto.getHospitalVisitingTime());

            foundHospital.setHospitalVisitingTime(filteredLocalDateTime);
        }
    }

    public void deleteHospital(User user, Long hospitalId) {
        Hospital foundHospital = findHospital(hospitalId);

        Protector validProtector = userCommonService.findProtector(user);
        Patient foundPatient = foundHospital.getPatient();
        userCommonService.checkUserIsProtectorOfPatient(validProtector, foundPatient);

        hospitalRepository.delete(foundHospital);
    }

    // 특정 환자에 대해 겹치는 병원 시간이 있는지 확인
    private LocalDateTime validateHospitalVisitingTime(Patient patient, LocalDateTime hospitalVisitingTime) {
        LocalDateTime filteredLocalDateTime = LocalDateTime.of(
                hospitalVisitingTime.toLocalDate(),
                LocalTime.of(hospitalVisitingTime.toLocalTime().getHour(),
                        hospitalVisitingTime.toLocalTime().getMinute(),
                        0
                )
        );

        Optional<Hospital> checkTargetHospital = hospitalRepository.findByPatientAndHospitalVisitingTime(patient, filteredLocalDateTime);
        if (checkTargetHospital.isPresent()) {
            throw new CustomErrorException(ErrorType.DuplicatedHospitalVisitingTimeError);
        }

        return filteredLocalDateTime;
    }


    private Hospital findHospital(Long hospitalId) {
        return hospitalRepository.findById(hospitalId).orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchHospitalError));
    }
}
