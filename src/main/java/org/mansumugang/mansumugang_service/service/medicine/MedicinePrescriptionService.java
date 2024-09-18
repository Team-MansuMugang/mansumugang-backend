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
import org.mansumugang.mansumugang_service.service.user.UserCommonService;
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

    private final MedicinePrescriptionRepository medicinePrescriptionRepository;

    private final ProfileChecker profileChecker;

    private final UserCommonService userCommonService;
    private final S3FileService s3FileService;
    private final FileService fileService;

    public MedicinePrescriptionListGet.Dto getMedicinePrescriptions(User user, Long patientId) {
        Protector validatedProtector = userCommonService.findProtector(user);
        Patient foundPatient = userCommonService.findPatient(patientId);
        userCommonService.checkUserIsProtectorOfPatient(validatedProtector, foundPatient);

        List<MedicinePrescription> foundMedicinePrescriptions = medicinePrescriptionRepository.findByPatientOrderByCreatedAtDesc(foundPatient);

        return MedicinePrescriptionListGet.Dto.fromEntity(patientId, foundMedicinePrescriptions, imageApiUrl);
    }

    public void saveMedicinePrescription(User user, MultipartFile medicinePrescriptionImage) {
        Patient validatedPatient = userCommonService.findPatient(user);

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
        MedicinePrescription foundMedicinePrescription = medicinePrescriptionRepository.findById(medicinePrescriptionId).
                orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchMedicinePrescriptionError));

        Protector validatedProtector = userCommonService.findProtector(user);
        Patient foundPatient = foundMedicinePrescription.getPatient();
        userCommonService.checkUserIsProtectorOfPatient(validatedProtector, foundPatient);

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
}
