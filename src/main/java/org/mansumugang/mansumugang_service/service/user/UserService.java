package org.mansumugang.mansumugang_service.service.user;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mansumugang.mansumugang_service.constant.ErrorType;
import org.mansumugang.mansumugang_service.constant.FileType;
import org.mansumugang.mansumugang_service.constant.InternalErrorType;
import org.mansumugang.mansumugang_service.domain.community.Post;
import org.mansumugang.mansumugang_service.domain.community.PostImage;
import org.mansumugang.mansumugang_service.domain.medicine.Medicine;
import org.mansumugang.mansumugang_service.domain.medicine.MedicinePrescription;
import org.mansumugang.mansumugang_service.domain.record.Record;
import org.mansumugang.mansumugang_service.domain.user.Patient;
import org.mansumugang.mansumugang_service.domain.user.Protector;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.dto.user.familyMember.FamilyMemberInquiry;
import org.mansumugang.mansumugang_service.dto.user.infoDelete.PatientInfoDelete;
import org.mansumugang.mansumugang_service.dto.user.infoDelete.ProtectorInfoDelete;
import org.mansumugang.mansumugang_service.dto.user.infoUpdate.PatientInfoUpdate;
import org.mansumugang.mansumugang_service.dto.user.infoUpdate.ProtectorInfoUpdate;
import org.mansumugang.mansumugang_service.dto.user.inquiry.PatientInfoInquiry;
import org.mansumugang.mansumugang_service.dto.user.inquiry.PatientInquiry;
import org.mansumugang.mansumugang_service.dto.user.inquiry.ProtectorInfoInquiry;
import org.mansumugang.mansumugang_service.exception.CustomErrorException;
import org.mansumugang.mansumugang_service.exception.InternalErrorException;
import org.mansumugang.mansumugang_service.repository.*;
import org.mansumugang.mansumugang_service.service.file.FileService;
import org.mansumugang.mansumugang_service.service.file.GeneralFileService;
import org.mansumugang.mansumugang_service.service.file.S3FileService;
import org.mansumugang.mansumugang_service.utils.ProfileChecker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    @Value("${file.upload.image.api}")
    private String imageApiUrl;

    private final PatientRepository patientRepository;
    private final ProtectorRepository protectorRepository;

    private final MedicineRepository medicineRepository;
    private final MedicinePrescriptionRepository medicinePrescriptionRepository;
    private final RecordRepository recordRepository;

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;

    private final FileService fileService;
    private final UserCommonService userCommonService;

    public PatientInquiry.Dto getPatientsByProtector(User user) {

        Protector validProtector = userCommonService.findProtector(user);

        List<Patient> foundPatients = getAllPatientsByProtectorId(validProtector);

        return PatientInquiry.Dto.fromEntity(foundPatients, imageApiUrl);

    }

    public ProtectorInfoInquiry.Dto getProtectorOwnInfo(User user) {

        Protector validProtector = userCommonService.findProtector(user);

        return ProtectorInfoInquiry.Dto.fromEntity(validProtector, imageApiUrl);

    }

    public PatientInfoInquiry.Dto getPatientOwnInfo(User user) {

        Patient validPatient = userCommonService.findPatient(user);

        return PatientInfoInquiry.Dto.fromEntity(validPatient, imageApiUrl);

    }

    public FamilyMemberInquiry.Dto getFamilyMember(User user) {

        Patient validPatient = userCommonService.findPatient(user);

        Protector foundProtector = protectorRepository.findById(validPatient.getProtector().getId())
                .orElseThrow(() -> new CustomErrorException(ErrorType.UserNotFoundError));

        List<Patient> foundPatients = patientRepository.findByProtector_id(foundProtector.getId())
                .stream()
                .filter(patient -> !patient.getId().equals(validPatient.getId()))
                .collect(toList());

        return FamilyMemberInquiry.Dto.of(validPatient, foundProtector, foundPatients, imageApiUrl);

    }


    @Transactional
    public ProtectorInfoUpdate.Dto updateProtectorInfo(User user, Long protectorId, ProtectorInfoUpdate.Request request) {

        Protector validProtector = userCommonService.findProtector(user);

        Protector foundProtector = protectorRepository.findById(protectorId)
                .orElseThrow(() -> new CustomErrorException(ErrorType.UserNotFoundError));

        if (!validProtector.getId().equals(foundProtector.getId())) {
            throw new CustomErrorException(ErrorType.AccessDeniedError);
        }

        String newNickname = request.getNickname();

        if (!validProtector.getNickname().equals(newNickname)) {
            if (protectorRepository.findByNickname(newNickname).isPresent()) {
                throw new CustomErrorException(ErrorType.DuplicatedNicknameError);
            }
        }

        foundProtector.update(request, newNickname);

        return ProtectorInfoUpdate.Dto.fromEntity(validProtector);

    }

    @Transactional
    public PatientInfoUpdate.Dto updatePatientInfo(User user, Long patientId, PatientInfoUpdate.Request request) {

        Patient validPatient = userCommonService.findPatient(user);

        Patient foundPatient = patientRepository.findById(patientId)
                .orElseThrow(() -> new CustomErrorException(ErrorType.UserNotFoundError));

        if (!validPatient.getId().equals(foundPatient.getId())) {
            throw new CustomErrorException(ErrorType.AccessDeniedError);
        }

        foundPatient.update(request);

        return PatientInfoUpdate.Dto.fromEntity(validPatient);

    }

    @Transactional
    public PatientInfoDelete.Dto deletePatientInfo(User user, Long patientId) {

        Patient validPatient = userCommonService.findPatient(user);

        Patient foundPatient = patientRepository.findById(patientId)
                .orElseThrow(() -> new CustomErrorException(ErrorType.UserNotFoundError));

        String username = foundPatient.getUsername();
        String name = foundPatient.getName();
        String usertype = foundPatient.getUsertype();

        if (!validPatient.getId().equals(foundPatient.getId())) {
            throw new CustomErrorException(ErrorType.AccessDeniedError);
        }

        List<Medicine> foundMedicines = medicineRepository.findAllByPatientId(validPatient.getId());
        for (Medicine foundMedicine : foundMedicines) {

            String medicineImageName = foundMedicine.getMedicineImageName();
            if (medicineImageName != null) {
                try {
                    fileService.deleteImageFile(medicineImageName);
                } catch (Exception e) {
                    throw new CustomErrorException(ErrorType.InternalServerError);
                }
            }
        }

        List<MedicinePrescription> foundPrescriptions = medicinePrescriptionRepository.findAllByPatientId(validPatient.getId());
        for (MedicinePrescription foundPrescription : foundPrescriptions) {

            String prescriptionImageName = foundPrescription.getMedicinePrescriptionImageName();
            if (prescriptionImageName != null) {
                try {
                    fileService.deleteImageFile(prescriptionImageName);
                } catch (Exception e) {
                    throw new CustomErrorException(ErrorType.InternalServerError);
                }
            }
        }

        List<Record> foundRecords = recordRepository.findAllByPatientId(validPatient.getId());
        for (Record foundRecord : foundRecords) {

            String recordFileName = foundRecord.getFilename();
            try {
                fileService.deleteAudioFile(recordFileName);
            } catch (Exception e) {
                throw new CustomErrorException(ErrorType.InternalServerError);
            }

        }

        patientRepository.delete(foundPatient);

        return PatientInfoDelete.Dto.fromEntity(username, name, usertype);

    }

    @Transactional
    public ProtectorInfoDelete.Dto deleteProtectorInfo(User user, Long protectorId) {

        Protector validProtector = userCommonService.findProtector(user);

        Protector foundProtector = protectorRepository.findById(protectorId)
                .orElseThrow(() -> new CustomErrorException(ErrorType.UserNotFoundError));

        String username = validProtector.getUsername();
        String name = validProtector.getName();
        String usertype = validProtector.getUsertype();

        if (!validProtector.getId().equals(foundProtector.getId())) {
            throw new CustomErrorException(ErrorType.AccessDeniedError);
        }

        List<Patient> foundPatients = patientRepository.findByProtector_id(protectorId);
        if (!foundPatients.isEmpty()) {
            throw new CustomErrorException(ErrorType.ProtectorHasActivePatientsError);
        }

        List<Post> foundPosts = postRepository.findAllByProtectorId(validProtector.getId());
        for (Post foundPost : foundPosts) {

            List<PostImage> foundPostImages = postImageRepository.findPostImageByPostId(foundPost.getId());
            for (PostImage foundPostImage : foundPostImages) {

                String postImageName = foundPostImage.getImageName();
                if (postImageName != null) {
                    try {
                       fileService.deleteImageFile(postImageName);
                    } catch (Exception e) {
                        throw new CustomErrorException(ErrorType.InternalServerError);
                    }
                }

            }
        }

        protectorRepository.delete(foundProtector);

        return ProtectorInfoDelete.Dto.fromEntity(username, name, usertype);
    }

    @Transactional
    public void updatePatientProfileImage(User user, MultipartFile userProfileImage) {
        Patient validatedPatient =  userCommonService.findPatient(user);

        Patient foundPatient = patientRepository.findById(validatedPatient.getId())
                .orElseThrow(() -> new CustomErrorException(ErrorType.UserNotFoundError));

        String newUserProfileImageName = saveProfileImage(userProfileImage);

        if (foundPatient.getProfileImageName() != null) {
            deleteProfileImage(foundPatient.getProfileImageName());
        }

        foundPatient.setProfileImageName(newUserProfileImageName);
    }

    @Transactional
    public void updateProtectorProfileImage(User user, MultipartFile userProfileImage) {
        Protector validatedProtector = userCommonService.findProtector(user);

        Protector foundProtector = protectorRepository.findById(validatedProtector.getId())
                .orElseThrow(() -> new CustomErrorException(ErrorType.UserNotFoundError));

        String newUserProfileImageName = saveProfileImage(userProfileImage);

        if (foundProtector.getProfileImageName() != null) {
            deleteProfileImage(foundProtector.getProfileImageName());
        }

        foundProtector.setProfileImageName(newUserProfileImageName);
    }

    @Transactional
    public void deleteProtectorProfileImage(User user) {
        Protector validatedProtector = userCommonService.findProtector(user);

        Protector foundProtector = protectorRepository.findById(validatedProtector.getId())
                .orElseThrow(() -> new CustomErrorException(ErrorType.UserNotFoundError));

        if (foundProtector.getProfileImageName() != null) {
            deleteProfileImage(foundProtector.getProfileImageName());
            foundProtector.setProfileImageName(null);
        } else {
            throw new CustomErrorException(ErrorType.NoUserProfileImageError);
        }

    }

    @Transactional
    public void deletePatientProfileImage(User user) {
        Patient validatedPatient =  userCommonService.findPatient(user);

        Patient foundPatient = patientRepository.findById(validatedPatient.getId())
                .orElseThrow(() -> new CustomErrorException(ErrorType.UserNotFoundError));

        if (foundPatient.getProfileImageName() != null) {
            deleteProfileImage(foundPatient.getProfileImageName());
            foundPatient.setProfileImageName(null);
        } else {
            throw new CustomErrorException(ErrorType.NoUserProfileImageError);
        }
    }

    private String saveProfileImage(MultipartFile profileImage) {
        try {
            return fileService.saveImageFile(profileImage);
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

        throw new CustomErrorException(ErrorType.InternalServerError);
    }

    private void deleteProfileImage(String imageName) {
        try {
            fileService.deleteImageFile(imageName);
        } catch (Exception e) {
            throw new CustomErrorException(ErrorType.InternalServerError);
        }
    }


    private List<Patient> getAllPatientsByProtectorId(Protector validProtector) {
        List<Patient> foundPatients = patientRepository.findByProtector_id(validProtector.getId());

        if (foundPatients.isEmpty()) {
            throw new CustomErrorException(ErrorType.UserNotFoundError);
        }

        return foundPatients;
    }


}
