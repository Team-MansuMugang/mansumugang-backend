package org.mansumugang.mansumugang_service.controller.medicine;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.dto.medicineIntake.MedicineIntakeToggle;
import org.mansumugang.mansumugang_service.service.medicine.MedicineIntakeService;
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
public class MedicineIntakeController {
    private final MedicineIntakeService medicineIntakeService;

    @PostMapping("/toggle")
    public ResponseEntity<MedicineIntakeToggle.Response> toggleMedicineIntake(
            @AuthenticationPrincipal User patient,
            @Valid @RequestBody MedicineIntakeToggle.Request requestDto){
        MedicineIntakeToggle.Dto medicineIntakeToggleDto = medicineIntakeService.toggleMedicineIntakeStatus(patient, requestDto);
        return new ResponseEntity<>(MedicineIntakeToggle.Response.fromDto(medicineIntakeToggleDto), HttpStatus.CREATED);
    }
}
