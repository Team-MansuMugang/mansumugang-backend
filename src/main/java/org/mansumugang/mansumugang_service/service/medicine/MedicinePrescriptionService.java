package org.mansumugang.mansumugang_service.service.medicine;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mansumugang.mansumugang_service.constant.ErrorType;
import org.mansumugang.mansumugang_service.constant.FileType;
import org.mansumugang.mansumugang_service.domain.medicine.MedicinePrescription;
import org.mansumugang.mansumugang_service.domain.user.Patient;
import org.mansumugang.mansumugang_service.domain.user.Protector;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.dto.medicine.MedicinePrescriptionListGet;
import org.mansumugang.mansumugang_service.exception.CustomErrorException;
import org.mansumugang.mansumugang_service.repository.MedicinePrescriptionRepository;
import org.mansumugang.mansumugang_service.repository.PatientRepository;
import org.mansumugang.mansumugang_service.service.fileService.FileService;
import org.mansumugang.mansumugang_service.service.fileService.S3FileService;
import org.mansumugang.mansumugang_service.utils.ProfileChecker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MedicinePrescriptionService {
    @Value("${file.upload.image.api}")
    private String imageApiUrl;

    private final PatientRepository patientRepository;
    private final MedicinePrescriptionRepository medicinePrescriptionRepository;

    private final ProfileChecker profileChecker;

    private final S3FileService s3FileService;
    private final FileService fileService;

    public MedicinePrescriptionListGet.Dto getMedicinePrescriptions(User user, Long patientId) {
        Protector validatedProtector = validateProtector(user);
        Patient foundPatient = findPatient(patientId);
        checkUserIsProtectorOfPatient(validatedProtector, foundPatient);

        List<MedicinePrescription> foundMedicinePrescriptions = medicinePrescriptionRepository.findByPatientOrderByCreatedAtDesc(foundPatient);

        return MedicinePrescriptionListGet.Dto.fromEntity(foundMedicinePrescriptions, imageApiUrl);
    }

    public void saveMedicinePrescription(User user, MultipartFile medicinePrescriptionImage) {
        Patient validatedPatient = validatePatient(user);

        String medicinePrescriptionImageName = null;
        if (medicinePrescriptionImage != null) {
            if (profileChecker.checkActiveProfile("prod")) {
                try {
                    medicinePrescriptionImageName = s3FileService.saveImageFile(medicinePrescriptionImage);
                } catch (IOException e) {
                    throw new CustomErrorException(ErrorType.InternalServerError);
                }
            } else {
                try {
                    medicinePrescriptionImageName = fileService.saveImageFiles(medicinePrescriptionImage);
                } catch (Exception e) {
                    throw new CustomErrorException(ErrorType.InternalServerError);
                }
            }
        }

        medicinePrescriptionRepository.save(MedicinePrescription.of(medicinePrescriptionImageName, validatedPatient));
    }

    public void deleteMedicinePrescription(User user, Long medicinePrescriptionId) {
        Protector validatedProtector = validateProtector(user);
        MedicinePrescription foundMedicinePrescription = medicinePrescriptionRepository.findById(medicinePrescriptionId).
                orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchMedicinePrescriptionError));

        checkUserIsProtectorOfPatient(validatedProtector, foundMedicinePrescription.getPatient());

        String originalMedicinePrescriptionImageName = foundMedicinePrescription.getMedicinePrescriptionImageName();
        medicinePrescriptionRepository.delete(foundMedicinePrescription);

        try {
            if (profileChecker.checkActiveProfile("prod")) {
                s3FileService.deleteFileFromS3(originalMedicinePrescriptionImageName, FileType.IMAGE);
            } else {
                fileService.deleteImageFile(originalMedicinePrescriptionImageName);
            }
        } catch (Exception e) {
            throw new CustomErrorException(ErrorType.InternalServerError);
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

    private Patient validatePatient(User user) {
        if (user == null) {
            throw new CustomErrorException(ErrorType.UserNotFoundError);
        }

        if (user instanceof Patient) {
            return (Patient) user;
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
