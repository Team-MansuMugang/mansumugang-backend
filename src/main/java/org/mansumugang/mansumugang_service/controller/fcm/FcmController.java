package org.mansumugang.mansumugang_service.controller.fcm;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.dto.fcm.FcmMessage;
import org.mansumugang.mansumugang_service.dto.fcm.FcmTokenSave;
import org.mansumugang.mansumugang_service.service.fcm.FcmService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fcm")
public class FcmController {

    private final FcmService fcmService;

    @PostMapping("/token/save")
    public ResponseEntity<FcmTokenSave.Response> fcmTokenSave(@AuthenticationPrincipal User user,
                                                              @RequestBody FcmTokenSave.Request request
    ){
        log.info("컨트롤러 호출");
        fcmService.saveFcmToken(user, request);

        return ResponseEntity.ok(FcmTokenSave.Response.createNewResponse());
    }


}
