package org.mansumugang.mansumugang_service.controller.medicine;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.dto.medicine.MedicineSchedule;
import org.mansumugang.mansumugang_service.dto.medicine.MedicineUpdate;
import org.mansumugang.mansumugang_service.dto.medicine.MedicineDelete;
import org.mansumugang.mansumugang_service.dto.medicine.MedicineSave;
import org.mansumugang.mansumugang_service.service.medicine.MedicineService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/medicine")
public class MedicineController {
    private final MedicineService medicineService;

    @GetMapping()
    public ResponseEntity<MedicineSchedule.Response> getMedicineByDate(@AuthenticationPrincipal User user,
                                                                  @RequestParam(required = true) String date,
                                                                  @RequestParam(required = true) Long patientId) {
        MedicineSchedule.Dto medicineByDate = medicineService.getMedicineByDate(user, patientId, date);
        return new ResponseEntity<>(MedicineSchedule.Response.fromDto(medicineByDate), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<MedicineSave.Response> saveMedicine(@AuthenticationPrincipal User user,
                                                                @Valid @RequestBody MedicineSave.Request requestDto) {
        medicineService.saveMedicine(user, requestDto);
        return new ResponseEntity<>(MedicineSave.Response.createNewResponse(), HttpStatus.CREATED);
    }

    @PatchMapping("/{medicineId}")
    public ResponseEntity<MedicineUpdate.Response> updateMedicine(@AuthenticationPrincipal User user,
                                                         @PathVariable Long medicineId,
                                                         @Valid @RequestBody MedicineUpdate.Request requestDto) {
        medicineService.updateMedicine(user, medicineId, requestDto);
        return new ResponseEntity<>(MedicineUpdate.Response.createNewResponse(), HttpStatus.CREATED);
    }

    @DeleteMapping("/{medicineId}")
    public ResponseEntity<MedicineDelete.Response> deleteMedicine(@AuthenticationPrincipal User user,
                                                                    @PathVariable Long medicineId,
                                                                    @Valid @RequestBody MedicineDelete.Request requestDto) {
        medicineService.deleteMedicine(user, medicineId, requestDto);
        return new ResponseEntity<>(MedicineDelete.Response.createNewResponse(), HttpStatus.CREATED);
    }
}
