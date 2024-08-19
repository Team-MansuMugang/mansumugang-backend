package org.mansumugang.mansumugang_service.controller.medicine;

import lombok.RequiredArgsConstructor;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.dto.medicine.MedicinePrescriptionDelete;
import org.mansumugang.mansumugang_service.dto.medicine.MedicinePrescriptionListGet;
import org.mansumugang.mansumugang_service.dto.medicine.MedicinePrescriptionSave;
import org.mansumugang.mansumugang_service.service.medicine.MedicinePrescriptionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/medicine/prescription")
public class MedicinePrescriptionController {
    private final MedicinePrescriptionService medicinePrescriptionService;

    @GetMapping()
    public ResponseEntity<MedicinePrescriptionListGet.Response> getMedicinePrescriptions(@AuthenticationPrincipal User user,
                                                                                         @RequestParam Long patientId) {
        MedicinePrescriptionListGet.Dto dto = medicinePrescriptionService.getMedicinePrescriptions(user, patientId);

        return new ResponseEntity<>(MedicinePrescriptionListGet.Response.fromDto(dto), HttpStatus.CREATED);
    }

    @PostMapping()
    public ResponseEntity<MedicinePrescriptionSave.Response> saveMedicinePrescription(@AuthenticationPrincipal User user,
                                                                                      @RequestPart(name = "image") MultipartFile medicinePrescriptionImage) {
        medicinePrescriptionService.saveMedicinePrescription(user, medicinePrescriptionImage);

        return new ResponseEntity<>(MedicinePrescriptionSave.Response.createNewResponse(), HttpStatus.CREATED);
    }

    @DeleteMapping("/{medicinePrescriptionId}")
    public ResponseEntity<MedicinePrescriptionDelete.Response> deleteMedicinePrescription(@AuthenticationPrincipal User user,
                                                                                          @PathVariable Long medicinePrescriptionId) {
        medicinePrescriptionService.deleteMedicinePrescription(user, medicinePrescriptionId);

        return new ResponseEntity<>(MedicinePrescriptionDelete.Response.createNewResponse(), HttpStatus.CREATED);
    }
}
