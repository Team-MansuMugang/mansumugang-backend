package org.mansumugang.mansumugang_service.controller.user;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.dto.user.PatientInquiry;
import org.mansumugang.mansumugang_service.service.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
