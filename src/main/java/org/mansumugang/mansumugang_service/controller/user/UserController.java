package org.mansumugang.mansumugang_service.controller.user;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.dto.user.familyMember.FamilyMemberInquiry;
import org.mansumugang.mansumugang_service.dto.user.infoDelete.PatientInfoDelete;
import org.mansumugang.mansumugang_service.dto.user.infoDelete.ProtectorInfoDelete;
import org.mansumugang.mansumugang_service.dto.user.infoUpdate.PatientInfoUpdate;
import org.mansumugang.mansumugang_service.dto.user.infoUpdate.ProtectorInfoUpdate;
import org.mansumugang.mansumugang_service.dto.user.inquiry.PatientInfoInquiry;
import org.mansumugang.mansumugang_service.dto.user.inquiry.PatientInquiry;
import org.mansumugang.mansumugang_service.dto.user.inquiry.ProtectorInfoInquiry;
import org.mansumugang.mansumugang_service.dto.user.userProfileImage.UserProfileImageDelete;
import org.mansumugang.mansumugang_service.dto.user.userProfileImage.UserProfileImageUpdate;
import org.mansumugang.mansumugang_service.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/inquiry/patients")
    public ResponseEntity<PatientInquiry.Response> getPatientsByProtector(@AuthenticationPrincipal User user) {

        PatientInquiry.Dto foundAllPatients = userService.getPatientsByProtector(user);

        return ResponseEntity.ok(PatientInquiry.Response.createNewResponse(foundAllPatients));
    }

    @GetMapping("/inquiry/protectorInfo")
    public ResponseEntity<ProtectorInfoInquiry.Response> getProtectorOwnInfo(@AuthenticationPrincipal User user) {

        ProtectorInfoInquiry.Dto foundOwnInfo = userService.getProtectorOwnInfo(user);

        return ResponseEntity.ok(ProtectorInfoInquiry.Response.createNewResponse(foundOwnInfo));
    }

    @GetMapping("/inquiry/patientInfo")
    public ResponseEntity<PatientInfoInquiry.Response> getPatientOwnInfo(@AuthenticationPrincipal User user) {

        PatientInfoInquiry.Dto foundOwnInfo = userService.getPatientOwnInfo(user);

        return ResponseEntity.ok(PatientInfoInquiry.Response.createNewResponse(foundOwnInfo));
    }

    @GetMapping("/inquiry/familyMember")
    public ResponseEntity<FamilyMemberInquiry.Response> getFamilyMember(@AuthenticationPrincipal User user) {


        FamilyMemberInquiry.Dto dto = userService.getFamilyMember(user);

        return ResponseEntity.ok(FamilyMemberInquiry.Response.createNewResponse(dto));
    }

    @PostMapping("/patient/profileImage")
    public ResponseEntity<UserProfileImageUpdate.Response> updatePatientProfileImage(@AuthenticationPrincipal User user,
                                                                                @RequestPart(name = "image") MultipartFile userProfileImage) {
        userService.updatePatientProfileImage(user, userProfileImage);
        return new ResponseEntity<>(UserProfileImageUpdate.Response.createNewResponse(), HttpStatus.CREATED);
    }

    @DeleteMapping("/patient/profileImage")
    public ResponseEntity<UserProfileImageDelete.Response> deletePatientProfileImage(@AuthenticationPrincipal User user) {
        userService.deletePatientProfileImage(user);
        return new ResponseEntity<>(UserProfileImageDelete.Response.createNewResponse(), HttpStatus.CREATED);
    }

    @PostMapping("/protector/profileImage")
    public ResponseEntity<UserProfileImageUpdate.Response> updateProtectorProfileImage(@AuthenticationPrincipal User user,
                                                                                  @RequestPart(name = "image") MultipartFile userProfileImage) {
        userService.updateProtectorProfileImage(user, userProfileImage);
        return new ResponseEntity<>(UserProfileImageUpdate.Response.createNewResponse(), HttpStatus.CREATED);
    }

    @DeleteMapping("/protector/profileImage")
    public ResponseEntity<UserProfileImageDelete.Response> deleteProtectorProfileImage(@AuthenticationPrincipal User user) {
        userService.deleteProtectorProfileImage(user);
        return new ResponseEntity<>(UserProfileImageDelete.Response.createNewResponse(), HttpStatus.CREATED);
    }

    // 유저 정보 수정
    @PatchMapping("/protector/{id}")
    public ResponseEntity<ProtectorInfoUpdate.Response> updateProtectorInfo(@AuthenticationPrincipal User user,
                                                                            @PathVariable(name = "id") Long protectorId,
                                                                            @Valid @RequestBody ProtectorInfoUpdate.Request request
    ) {
        ProtectorInfoUpdate.Dto dto = userService.updateProtectorInfo(user, protectorId, request);

        return new ResponseEntity<>(ProtectorInfoUpdate.Response.createNewResponse(dto), HttpStatus.CREATED);
    }


    @PatchMapping("/patient/{id}")
    public ResponseEntity<PatientInfoUpdate.Response> updatePatientInfo(@AuthenticationPrincipal User user,
                                                                        @PathVariable(name = "id") Long patientId,
                                                                        @Valid @RequestBody PatientInfoUpdate.Request request
    ) {

        PatientInfoUpdate.Dto dto = userService.updatePatientInfo(user, patientId, request);

        return new ResponseEntity<>(PatientInfoUpdate.Response.createNewResponse(dto), HttpStatus.CREATED);

    }

    @DeleteMapping("/patient/{id}")
    public ResponseEntity<PatientInfoDelete.Response> deletePatientInfo(@AuthenticationPrincipal User user,
                                                                        @PathVariable(name = "id") Long patientId) {

        PatientInfoDelete.Dto dto = userService.deletePatientInfo(user, patientId);

        return new ResponseEntity<>(PatientInfoDelete.Response.createNewResponse(dto), HttpStatus.CREATED);
    }

    @DeleteMapping("/protector/{id}")
    public ResponseEntity<ProtectorInfoDelete.Response> deleteProtectorInfo(@AuthenticationPrincipal User user,
                                                                            @PathVariable(name = "id") Long protectorId
    ) {
        ProtectorInfoDelete.Dto dto = userService.deleteProtectorInfo(user, protectorId);

        return new ResponseEntity<>(ProtectorInfoDelete.Response.createNewResponse(dto), HttpStatus.CREATED);
    }


}
