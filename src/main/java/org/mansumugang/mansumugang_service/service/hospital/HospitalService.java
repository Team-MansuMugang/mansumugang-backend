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
import org.mansumugang.mansumugang_service.repository.PatientRepository;
import org.springframework.stereotype.Service;

import static org.mansumugang.mansumugang_service.constant.LocationBoundary.*;
import static org.mansumugang.mansumugang_service.constant.LocationBoundary.EXTREME_EAST;

@Service
@RequiredArgsConstructor
@Transactional
public class HospitalService {
    private final HospitalRepository hospitalRepository;
    private final PatientRepository patientRepository;

    public HospitalDetailGet.Dto getHospitalDetail(User user, Long hospitalId) {
        Hospital foundHospital = hospitalRepository.findById(hospitalId).orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchHospitalError));

        Protector validProtector = validateProtector(user);
        Patient foundPatient = findPatient(foundHospital.getPatient().getId());
        checkUserIsProtectorOfPatient(validProtector, foundPatient);

        return HospitalDetailGet.Dto.fromEntity(foundHospital);
    }

    public void saveHospital(User user, HospitalSave.Request requestDto) {
        Protector validProtector = validateProtector(user);
        Patient foundPatient = findPatient(requestDto.getPatientId());
        checkUserIsProtectorOfPatient(validProtector, foundPatient);

        validateUserLocation(requestDto.getLatitude(), requestDto.getLongitude());

        hospitalRepository.save(Hospital.of(requestDto, foundPatient));
    }

    public void updateHospital(User user, Long hospitalId, HospitalUpdate.Request requestDto) {
        Hospital foundHospital = hospitalRepository.findById(hospitalId).orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchHospitalError));

        Protector validProtector = validateProtector(user);
        Patient foundPatient = findPatient(foundHospital.getPatient().getId());
        checkUserIsProtectorOfPatient(validProtector, foundPatient);

        if (requestDto.getHospitalName() != null) {
            foundHospital.setHospitalName(requestDto.getHospitalName());
        }

        if(requestDto.getHospitalAddress() != null) {
            if(requestDto.getLatitude() == null || requestDto.getLongitude() == null) {
                throw new CustomErrorException(ErrorType.NeedLatitudeAndLongitudeError);
            }

            validateUserLocation(requestDto.getLatitude(), requestDto.getLongitude());

            foundHospital.setHospitalAddress(requestDto.getHospitalAddress());
            foundHospital.setLatitude(requestDto.getLatitude());
            foundHospital.setLongitude(requestDto.getLongitude());
        }

        if (requestDto.getHospitalDescription() != null) {
            foundHospital.setHospitalDescription(requestDto.getHospitalDescription());
        }

        if(requestDto.getHospitalVisitingTime() != null) {
            foundHospital.setHospitalVisitingTime(requestDto.getHospitalVisitingTime());
        }
    }

    public void deleteHospital(User user, Long hospitalId) {
        Hospital foundHospital = hospitalRepository.findById(hospitalId).orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchHospitalError));

        Protector validProtector = validateProtector(user);
        Patient foundPatient = findPatient(foundHospital.getPatient().getId());
        checkUserIsProtectorOfPatient(validProtector, foundPatient);

        hospitalRepository.delete(foundHospital);
    }

    public void validateUserLocation(Double latitude, Double longitude) {
        if (!(EXTREME_SOUTH.getCoordinate() < latitude && latitude < EXTREME_NORTH.getCoordinate())
                || !(EXTREME_WEST.getCoordinate() < longitude && longitude < EXTREME_EAST.getCoordinate())){

            throw new CustomErrorException(ErrorType.OutOfBoundaryError);
        }
    }

    private Patient findPatient(Long patientId) {
        return patientRepository.findById(patientId).orElseThrow(() -> new CustomErrorException(ErrorType.UserNotFoundError));
    }

    private Protector validateProtector(User user) {
        if (user == null) {
            throw new CustomErrorException(ErrorType.UserNotFoundError);
        }

        if (user instanceof Protector) {
            return (Protector) user;
        }

        throw new CustomErrorException(ErrorType.AccessDeniedError);
    }

    private void checkUserIsProtectorOfPatient(Protector targetProtector, Patient patient) {
        // TODO: equals, hashcode 구현
        if (!patient.getProtector().getUsername().equals(targetProtector.getUsername())) {
            throw new CustomErrorException(ErrorType.AccessDeniedError);
        }
    }

}
