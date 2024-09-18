package org.mansumugang.mansumugang_service.service.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mansumugang.mansumugang_service.constant.ErrorType;
import org.mansumugang.mansumugang_service.domain.user.Patient;
import org.mansumugang.mansumugang_service.domain.user.Protector;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.exception.CustomErrorException;
import org.mansumugang.mansumugang_service.repository.PatientRepository;
import org.mansumugang.mansumugang_service.repository.ProtectorRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UserCommonService {
    private final PatientRepository patientRepository;
    private final ProtectorRepository protectorRepository;


    public Protector findProtector(User user) {
        if (user == null) {
            throw new CustomErrorException(ErrorType.UserNotFoundError);
        }

        if (user instanceof Protector) {
            return protectorRepository.findById(user.getId()).orElseThrow(() -> new CustomErrorException(ErrorType.AccessDeniedError));
        }

        throw new CustomErrorException(ErrorType.AccessDeniedError);
    }

    public Patient findPatient(Long patientId) {
        return patientRepository.findById(patientId).orElseThrow(() -> new CustomErrorException(ErrorType.UserNotFoundError));
    }


    public Patient findPatient(User user) {
        if (user == null) {
            throw new CustomErrorException(ErrorType.UserNotFoundError);
        }

        if (user instanceof Patient) {
            return patientRepository.findById(user.getId()).orElseThrow(() -> new CustomErrorException(ErrorType.AccessDeniedError));
        }

        throw new CustomErrorException(ErrorType.AccessDeniedError);
    }

    public void checkUserIsProtectorOfPatient(Protector targetProtector, Patient patient) {
        // TODO: equals, hashcode 구현
        if (!patient.getProtector().getUsername().equals(targetProtector.getUsername())) {
            throw new CustomErrorException(ErrorType.AccessDeniedError);
        }
    }
}
