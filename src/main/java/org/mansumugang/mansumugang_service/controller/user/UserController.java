package org.mansumugang.mansumugang_service.controller.user;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.dto.user.*;
import org.mansumugang.mansumugang_service.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/inquiry/patients")
    public ResponseEntity<PatientInquiry.Response> getPatientsByProtector(@AuthenticationPrincipal User user){

        PatientInquiry.Dto foundAllPatients = userService.getPatientsByProtector(user);

        return ResponseEntity.ok(PatientInquiry.Response.createNewResponse(foundAllPatients));
    }

    @GetMapping("/inquiry/protectorInfo")
    public ResponseEntity<ProtectorInfoInquiry.Response> getProtectorOwnInfo(@AuthenticationPrincipal User user){

        ProtectorInfoInquiry.Dto foundOwnInfo = userService.getProtectorOwnInfo(user);

        return ResponseEntity.ok(ProtectorInfoInquiry.Response.createNewResponse(foundOwnInfo));
    }

    @GetMapping("/inquiry/patientInfo")
    public ResponseEntity<PatientInfoInquiry.Response> getPatientOwnInfo(@AuthenticationPrincipal User user){

        PatientInfoInquiry.Dto foundOwnInfo = userService.getPatientOwnInfo(user);

        return ResponseEntity.ok(PatientInfoInquiry.Response.createNewResponse(foundOwnInfo));
    }

    // 유저 정보 수정
    @PatchMapping("/protector/{id}")
    public ResponseEntity<ProtectorInfoUpdate.Response> updateProtectorInfo(@AuthenticationPrincipal User user,
                                                                            @PathVariable(name = "id") Long protectorId,
                                                                            @Valid @RequestBody ProtectorInfoUpdate.Request request
    ){
        ProtectorInfoUpdate.Dto dto = userService.updateProtectorInfo(user, protectorId, request);

        return new ResponseEntity<>(ProtectorInfoUpdate.Response.createNewResponse(dto), HttpStatus.CREATED);
    }



    @PatchMapping("/patient/{id}")
    public ResponseEntity<PatientInfoUpdate.Response> updatePatientInfo(@AuthenticationPrincipal User user,
                                                                        @PathVariable(name = "id") Long patientId,
                                                                        @Valid @RequestBody PatientInfoUpdate.Request request
    ){

        PatientInfoUpdate.Dto dto = userService.updatePatientInfo(user, patientId, request);

        return new ResponseEntity<>(PatientInfoUpdate.Response.createNewResponse(dto), HttpStatus.CREATED);

    }


}
