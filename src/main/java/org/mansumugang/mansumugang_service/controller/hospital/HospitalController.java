package org.mansumugang.mansumugang_service.controller.hospital;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.dto.hospital.HospitalSave;
import org.mansumugang.mansumugang_service.service.hospital.HospitalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hospital")
public class HospitalController {
    private final HospitalService hospitalService;

    @PostMapping()
    public ResponseEntity<HospitalSave.Response> saveHospital(@AuthenticationPrincipal User user,
                                                              @Valid @RequestBody HospitalSave.Request requestDto) {
        hospitalService.saveHospital(user, requestDto);
        return new ResponseEntity<>(HospitalSave.Response.createNewResponse(), HttpStatus.CREATED);
    }
}
