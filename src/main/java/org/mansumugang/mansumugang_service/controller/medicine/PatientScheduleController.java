package org.mansumugang.mansumugang_service.controller.medicine;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.dto.patientScheduleToggle.PatientScheduleToggle;
import org.mansumugang.mansumugang_service.service.medicine.PatientScheduleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/medicine/intake")
public class PatientScheduleController {
    private final PatientScheduleService patientScheduleService;

    @PostMapping("/toggle")
    public ResponseEntity<PatientScheduleToggle.Response> togglePatientSchedule(
            @AuthenticationPrincipal User patient,
            @Valid @RequestBody PatientScheduleToggle.Request requestDto){
        PatientScheduleToggle.Dto medicineIntakeToggleDto = patientScheduleService.togglePatientSchedule(patient, requestDto);
        return new ResponseEntity<>(PatientScheduleToggle.Response.fromDto(medicineIntakeToggleDto), HttpStatus.CREATED);
    }
}
