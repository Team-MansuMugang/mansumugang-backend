package org.mansumugang.mansumugang_service.controller.auth;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mansumugang.mansumugang_service.dto.auth.logout.LogoutResponseDto;
import org.mansumugang.mansumugang_service.dto.auth.signup.*;
import org.mansumugang.mansumugang_service.dto.auth.token.ReissueTokenDto;
import org.mansumugang.mansumugang_service.dto.auth.token.ReissueTokenResponseDto;
import org.mansumugang.mansumugang_service.service.auth.LogoutService;
import org.mansumugang.mansumugang_service.service.auth.ReIssueTokenService;
import org.mansumugang.mansumugang_service.service.auth.SignupService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final SignupService signUpService;
    private final LogoutService logoutService;
    private final ReIssueTokenService reIssueTokenService;

    @PostMapping("/signup/patient")
    public ResponseEntity<SignUpResponseDto> patientSignup(
            @Valid @RequestBody PatientSignupRequestDto patientSignupRequestDto
    ){
        SignUpDto signUpDto = signUpService.patientSignup(patientSignupRequestDto);

        return new ResponseEntity<>(SignUpResponseDto.dtoToResponse(signUpDto), HttpStatus.CREATED);

    }

    @PostMapping("/signup/protector")
    public ResponseEntity<SignUpResponseDto> protectorSignup(
            @Valid @RequestBody ProtectorSignUpRequestDto protectorSignUpRequestDto
    ){

        SignUpDto signUpDto = signUpService.protectorSignup(protectorSignUpRequestDto);

        return new ResponseEntity<>(SignUpResponseDto.dtoToResponse(signUpDto), HttpStatus.CREATED);
    }

    @PostMapping("/check/username")
    public ResponseEntity<UsernameDuplicationCheckResponseDto> checkPatientUsernameDuplication(
            @Valid @RequestBody UsernameDuplicationCheckDto usernameDuplicationCheckDto
    ){

        signUpService.checkUsernameDuplication(usernameDuplicationCheckDto);

        return new ResponseEntity<>(new UsernameDuplicationCheckResponseDto(), HttpStatus.OK);

    }

    @PostMapping("/check/protectorUsername")
    public ResponseEntity<ProtectorUsernameCheckResponseDto> checkProtectorUsername(
            @Valid @RequestBody ProtectorUsernameCheckRequestDto protectorUsernameCheckRequestDto
    ){
        signUpService.checkProtectorUsername(protectorUsernameCheckRequestDto);

        return new ResponseEntity<>(new ProtectorUsernameCheckResponseDto(), HttpStatus.OK);
    }

    @PostMapping("/check/nickname")
    public ResponseEntity<NicknameDuplicationCheckResponseDto> checkNicknameDuplication(
            @Valid @RequestBody NicknameDuplicationCheckDto nicknameDuplicationCheckDto
    ){

        signUpService.checkNicknameDuplication(nicknameDuplicationCheckDto);

        return new ResponseEntity<>(new NicknameDuplicationCheckResponseDto(), HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponseDto> logout(
            @RequestHeader("Authorization-refresh") String refreshToken
    ){
        logoutService.logout(refreshToken);

        return new ResponseEntity<>(new LogoutResponseDto(), HttpStatus.CREATED);

    }

    @PostMapping("/refreshToken")
    public ResponseEntity<ReissueTokenResponseDto> reIssueToken(
        @RequestHeader("Authorization") String accessToken,
        @RequestHeader("Authorization-refresh") String refreshToken
    ){
        ReissueTokenDto reissueTokenDto = reIssueTokenService.reissueToken(accessToken, refreshToken);

        return new ResponseEntity<>(ReissueTokenResponseDto.fromDto(reissueTokenDto), HttpStatus.CREATED);
    }
}
