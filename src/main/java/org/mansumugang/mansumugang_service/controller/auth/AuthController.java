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

    // 로그인 관련

    // 환자 회원가입
    @PostMapping("/signup/patient")
    public ResponseEntity<SignUpResponseDto> patientSignup(
            @Valid @RequestBody PatientSignupRequestDto patientSignupRequestDto
    ){
        log.info("환자 회원가입 시작");
        SignUpDto signUpDto = signUpService.patientSignup(patientSignupRequestDto);

        log.info("환자 회원가입 완료 및 응답 전송");
        return new ResponseEntity<>(SignUpResponseDto.dtoToResponse(signUpDto), HttpStatus.CREATED);

    }

    // 보호자 회원가입
    @PostMapping("/signup/protector")
    public ResponseEntity<SignUpResponseDto> protectorSignup(
            @Valid @RequestBody ProtectorSignUpRequestDto protectorSignUpRequestDto
    ){

        log.info("보호자 회원가입 시작");
        SignUpDto signUpDto = signUpService.protectorSignup(protectorSignUpRequestDto);

        log.info("보호자 회원가입 완료 및 응답 전송");
        return new ResponseEntity<>(SignUpResponseDto.dtoToResponse(signUpDto), HttpStatus.CREATED);
    }

    // 회원가입 시  id 중복체크 버튼
    @PostMapping("/check/username")
    public ResponseEntity<UsernameDuplicationCheckResponseDto> checkPatientUsernameDuplication(
            @Valid @RequestBody UsernameDuplicationCheckDto usernameDuplicationCheckDto
    ){

        // 아이디 중복 확인
        signUpService.checkUsernameDuplication(usernameDuplicationCheckDto);

        // 성공 로직(responseDto 로 변환 및 반환)
        return new ResponseEntity<>(new UsernameDuplicationCheckResponseDto(), HttpStatus.OK);

    }

    @PostMapping("/check/protectorUsername")
    public ResponseEntity<ProtectorUsernameCheckResponseDto> checkProtectorUsername(
            @Valid @RequestBody ProtectorUsernameCheckRequestDto protectorUsernameCheckRequestDto
    ){
        signUpService.checkProtectorUsername(protectorUsernameCheckRequestDto);

        return new ResponseEntity<>(new ProtectorUsernameCheckResponseDto(), HttpStatus.OK);
    }

    @PostMapping("/check/nickname") // 유저 닉네임 중복확인 버튼
    public ResponseEntity<NicknameDuplicationCheckResponseDto> checkNicknameDuplication(
            @Valid @RequestBody NicknameDuplicationCheckDto nicknameDuplicationCheckDto
    ){

        // 닉네임 중복 확인
        signUpService.checkNicknameDuplication(nicknameDuplicationCheckDto);

        // 성공 로직
        return new ResponseEntity<>(new NicknameDuplicationCheckResponseDto(), HttpStatus.OK);
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<LogoutResponseDto> logout(
            @RequestHeader("Authorization-refresh") String refreshToken
    ){
        logoutService.logout(refreshToken);

        return new ResponseEntity<>(new LogoutResponseDto(), HttpStatus.CREATED);

    }

    // 토큰 재발행
    @PostMapping("/refreshToken")
    public ResponseEntity<ReissueTokenResponseDto> reIssueToken(
        @RequestHeader("Authorization") String accessToken,
        @RequestHeader("Authorization-refresh") String refreshToken
    ){
        ReissueTokenDto reissueTokenDto = reIssueTokenService.reissueToken(accessToken, refreshToken);

        return new ResponseEntity<>(ReissueTokenResponseDto.fromDto(reissueTokenDto), HttpStatus.CREATED);
    }
}
