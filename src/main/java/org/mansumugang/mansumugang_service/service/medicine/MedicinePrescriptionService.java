package org.mansumugang.mansumugang_service.service.medicine;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mansumugang.mansumugang_service.constant.ErrorType;
import org.mansumugang.mansumugang_service.constant.InternalErrorType;
import org.mansumugang.mansumugang_service.domain.medicine.MedicinePrescription;
import org.mansumugang.mansumugang_service.domain.user.Patient;
import org.mansumugang.mansumugang_service.domain.user.Protector;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.dto.medicine.MedicinePrescriptionListGet;
import org.mansumugang.mansumugang_service.exception.CustomErrorException;
import org.mansumugang.mansumugang_service.exception.InternalErrorException;
import org.mansumugang.mansumugang_service.repository.MedicinePrescriptionRepository;
import org.mansumugang.mansumugang_service.service.file.FileService;
import org.mansumugang.mansumugang_service.service.user.UserCommonService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MedicinePrescriptionService {
    @Value("${file.upload.image.api}")
    private String imageApiUrl;

    private final MedicinePrescriptionRepository medicinePrescriptionRepository;

    private final UserCommonService userCommonService;
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

        if (medicinePrescriptionImage != null) {
            try {
                String medicinePrescriptionImageName = fileService.saveImageFile(medicinePrescriptionImage);
                medicinePrescriptionRepository.save(MedicinePrescription.of(medicinePrescriptionImageName, validatedPatient));
            } catch (InternalErrorException e) {
                if(e.getInternalErrorType() == InternalErrorType.EmptyFileError) {
                    throw new CustomErrorException(ErrorType.NoImageFileError);
                }

                if(e.getInternalErrorType() == InternalErrorType.InvalidFileExtension) {
                    throw new CustomErrorException(ErrorType.InvalidImageFileExtension);
                }

                if(e.getInternalErrorType() == InternalErrorType.FileSaveError) {
                    throw new CustomErrorException(ErrorType.InternalServerError);
                }
            }
        }

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
            fileService.deleteImageFile(originalMedicinePrescriptionImageName);
        } catch (Exception e) {
            throw new CustomErrorException(ErrorType.InternalServerError);
        }
    }
}
