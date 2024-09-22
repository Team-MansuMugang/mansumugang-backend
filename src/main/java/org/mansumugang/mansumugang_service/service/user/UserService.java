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

    public PatientInquiry.Dto getPatientsByProtector(User user) {

        // 1. AuthenticationPrincipal 로 넘겨받은 user 가 보호자 객체인지 검증
        Protector validProtector = validateProtector(user);

        // 2. 검증된 보호자 객체로 보호자의 환자 찾기.
        List<Patient> foundPatients = getAllPatientsByProtectorId(validProtector);

        return PatientInquiry.Dto.fromEntity(foundPatients, imageApiUrl);

    }

    public ProtectorInfoInquiry.Dto getProtectorOwnInfo(User user) {

        // 1. AuthenticationPrincipal 로 넘겨받은 user 가 보호자 객체인지 검증
        Protector validProtector = validateProtector(user);

        return ProtectorInfoInquiry.Dto.fromEntity(validProtector, imageApiUrl);

    }

    public PatientInfoInquiry.Dto getPatientOwnInfo(User user) {

        // 1. AuthenticationPrincipal 로 넘겨받은 user 가 보호자 객체인지 검증
        Patient validPatient = validatePatient(user);

        return PatientInfoInquiry.Dto.fromEntity(validPatient, imageApiUrl);

    }

    public FamilyMemberInquiry.Dto getFamilyMember(User user) {

        // 1. AuthenticationPrincipal 로 넘격받은 user 가 환자 객체인지 검증
        Patient validPatient = validatePatient(user);

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

        // 1.User 가 보호자 객체인지 검증
        Protector validProtector = validateProtector(user);

        Protector foundProtector = protectorRepository.findById(protectorId)
                .orElseThrow(() -> new CustomErrorException(ErrorType.UserNotFoundError));

        // 2. 검증된 보호자 객체의 id와 경로변수의 protectorId가 동일한지 검증. => 아니면 예외 처리
        if (!validProtector.getId().equals(foundProtector.getId())) {
            throw new CustomErrorException(ErrorType.AccessDeniedError);
        }

        // 3. 변경하려는 닉네임이 기존 본인의 닉네임과 동일하지 않거나 다른 닉네임이 존재한다면 => 예외 처리.
        String newNickname = request.getNickname();

        if (!validProtector.getNickname().equals(newNickname)) {
            if (protectorRepository.findByNickname(newNickname).isPresent()) {
                throw new CustomErrorException(ErrorType.DuplicatedNicknameError);
            }
        }

        // 4. 유저 정보 업데이트 시작.
        foundProtector.update(request, newNickname);

        return ProtectorInfoUpdate.Dto.fromEntity(validProtector);

    }

    @Transactional
    public PatientInfoUpdate.Dto updatePatientInfo(User user, Long patientId, PatientInfoUpdate.Request request) {

        // 1. User 가 환자 객체인지 검증
        Patient validPatient = validatePatient(user);

        // 2. 경로변수로 받은 환자 객체 조회
        Patient foundPatient = patientRepository.findById(patientId)
                .orElseThrow(() -> new CustomErrorException(ErrorType.UserNotFoundError));

        // 3. validPatent와 foundPatent가 동일한 환자이어야함.
        if (!validPatient.getId().equals(foundPatient.getId())) {
            throw new CustomErrorException(ErrorType.AccessDeniedError);
        }

        // 4. 유저 정보 업데이트 시작.
        foundPatient.update(request);

        return PatientInfoUpdate.Dto.fromEntity(validPatient);

    }

    @Transactional
    public PatientInfoDelete.Dto deletePatientInfo(User user, Long patientId) {

        // 1. 환자 객체 검증
        Patient validPatient = validatePatient(user);

        // 2. 회원 탈퇴를 진행할 유저와 경로변수로 찾은 환자 정보가 같은지 검증
        Patient foundPatient = patientRepository.findById(patientId)
                .orElseThrow(() -> new CustomErrorException(ErrorType.UserNotFoundError));

        String username = foundPatient.getUsername();
        String name = foundPatient.getName();
        String usertype = foundPatient.getUsertype();

        if (!validPatient.getId().equals(foundPatient.getId())) {
            throw new CustomErrorException(ErrorType.AccessDeniedError);
        }

        // 3. 약 이미지, 처방전, 음성녹음 삭제
        // 3.1. 약이미지 이름 찾기 -> 약이미지 파일 삭제
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

        // 3.2 처방전 이미지 이름 찾기 -> 이미지 파일 삭제
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

        // 3.3 음성녹음 파일 이름 찾기 -> 음성녹음 파일 삭제
        List<Record> foundRecords = recordRepository.findAllByPatientId(validPatient.getId());
        for (Record foundRecord : foundRecords) {

            String recordFileName = foundRecord.getFilename();
            try {
                fileService.deleteAudioFile(recordFileName);
            } catch (Exception e) {
                throw new CustomErrorException(ErrorType.InternalServerError);
            }

        }

        // 4. 회원 정보와 관련된 모든 정보 삭제 진행
        patientRepository.delete(foundPatient);

        return PatientInfoDelete.Dto.fromEntity(username, name, usertype);

    }

    @Transactional
    public ProtectorInfoDelete.Dto deleteProtectorInfo(User user, Long protectorId) {

        // 1. user가 보호자 객체인지 검증
        Protector validProtector = validateProtector(user);

        // 2. 경로변수로 찾은 보호자 정보와 user의 보호자 정보가 일치하는지 검증
        Protector foundProtector = protectorRepository.findById(protectorId)
                .orElseThrow(() -> new CustomErrorException(ErrorType.UserNotFoundError));

        String username = validProtector.getUsername();
        String name = validProtector.getName();
        String usertype = validProtector.getUsertype();

        if (!validProtector.getId().equals(foundProtector.getId())) {
            throw new CustomErrorException(ErrorType.AccessDeniedError);
        }

        // 3. 보호자의 환자가 전부 탈퇴가 진행되지 않았다면 보호자는 탈퇴 불가.
        List<Patient> foundPatients = patientRepository.findByProtector_id(protectorId);
        if (!foundPatients.isEmpty()) {
            throw new CustomErrorException(ErrorType.ProtectorHasActivePatientsError);
        }

        // 4. 전부 탈퇴 되었다면 삭제 진행. => 보호자는 게시물, 게시물 이미지, 댓글, 대댓글 등등 삭제
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

        // 5. 보호자 정보 및 관련 정보들 DB에서 삭제
        protectorRepository.delete(foundProtector);

        return ProtectorInfoDelete.Dto.fromEntity(username, name, usertype);
    }

    @Transactional
    public void updatePatientProfileImage(User user, MultipartFile userProfileImage) {
        Patient validatedPatient = validatePatient(user);

        Patient foundPatient = patientRepository.findById(validatedPatient.getId())
                .orElseThrow(() -> new CustomErrorException(ErrorType.UserNotFoundError));

        // 새 프로필 이미지 저장
        String newUserProfileImageName = saveProfileImage(userProfileImage);

        // 프로필 이미지가 존재할 경우 삭제
        if (foundPatient.getProfileImageName() != null) {
            deleteProfileImage(foundPatient.getProfileImageName());
        }

        // 데이터베이스 내 프로필 이미지 이름 변경
        foundPatient.setProfileImageName(newUserProfileImageName);
    }

    @Transactional
    public void updateProtectorProfileImage(User user, MultipartFile userProfileImage) {
        Protector validatedProtector = validateProtector(user);

        Protector foundProtector = protectorRepository.findById(validatedProtector.getId())
                .orElseThrow(() -> new CustomErrorException(ErrorType.UserNotFoundError));

        // 새 프로필 이미지 저장
        String newUserProfileImageName = saveProfileImage(userProfileImage);

        // 프로필 이미지가 존재할 경우 삭제
        if (foundProtector.getProfileImageName() != null) {
            deleteProfileImage(foundProtector.getProfileImageName());
        }

        // 데이터베이스 내 프로필 이미지 이름 변경
        foundProtector.setProfileImageName(newUserProfileImageName);
    }

    @Transactional
    public void deleteProtectorProfileImage(User user) {
        Protector validatedProtector = validateProtector(user);

        Protector foundProtector = protectorRepository.findById(validatedProtector.getId())
                .orElseThrow(() -> new CustomErrorException(ErrorType.UserNotFoundError));

        // 프로필 이미지가 존재할 경우 삭제
        if (foundProtector.getProfileImageName() != null) {
            deleteProfileImage(foundProtector.getProfileImageName());
            foundProtector.setProfileImageName(null);
        } else {
            throw new CustomErrorException(ErrorType.NoUserProfileImageError);
        }

    }

    @Transactional
    public void deletePatientProfileImage(User user) {
        Patient validatedPatient = validatePatient(user);

        Patient foundPatient = patientRepository.findById(validatedPatient.getId())
                .orElseThrow(() -> new CustomErrorException(ErrorType.UserNotFoundError));

        // 프로필 이미지가 존재할 경우 삭제
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


    private List<Patient> getAllPatientsByProtectorId(Protector validProtector) {
        List<Patient> foundPatients = patientRepository.findByProtector_id(validProtector.getId());

        if (foundPatients.isEmpty()) {
            throw new CustomErrorException(ErrorType.UserNotFoundError);
        }

        return foundPatients;
    }


}
