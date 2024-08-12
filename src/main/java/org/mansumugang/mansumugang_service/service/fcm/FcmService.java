package org.mansumugang.mansumugang_service.service.fcm;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mansumugang.mansumugang_service.constant.ErrorType;
import org.mansumugang.mansumugang_service.domain.fcm.FcmToken;
import org.mansumugang.mansumugang_service.domain.user.Protector;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.dto.fcm.FcmTokenSave;
import org.mansumugang.mansumugang_service.exception.CustomErrorException;
import org.mansumugang.mansumugang_service.repository.FcmTokenRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {

    private final FirebaseMessaging firebaseMessaging;
    private final FcmTokenRepository fcmTokenRepository;

    @Transactional
    public void saveFcmToken(User user, FcmTokenSave.Request request){

        log.info("서비스 호출");

        // 1. 받은 user 가 Protector 인지 확인.
        Protector validProtector = validateProtector(user);

        // 2. 받아온 요청의 fcmToken 검증.
        String validFcmToken = validateFcmToken(request);

        // 3. 검증 완료시 DB에 저장
        fcmTokenRepository.save(FcmToken.of(validFcmToken, validProtector));

    }

    public void sendMessage(Message message){
//        String userIIDToken = "fGng3MP7GbkP3YpWJDm_rV:APA91bGHkfOTeS5Nsuu2lCW0zt5F6cTN81VuFQoIxuK40EkVbkQE6tVqwUCAtJ73QF87XUM0QxjnvopVhETTtnfIMIoyio8u0ojRQ4mHONd8d7Q_XyJ1V8WgxwddOjgIkaypGIE1YuDX";

        try {
            String response = firebaseMessaging.send(message);
            log.info(response);
        } catch (FirebaseMessagingException e) {
            log.error(e.getMessage());

            // TODO : 앱이 정상적으로 홈화면에 추가되지 않으면 푸시알림이 정상적으로 보내지지 않음. 이에 대한 예외처리가 필요함.
        }
    }

    private Protector validateProtector(User user) {
        log.info("AuthenticationPrincipal 로 받은 유저 객체가 보호자 객체인지 검증 시작");
        if (user == null) {
            throw new CustomErrorException(ErrorType.UserNotFoundError);
        }

        if (user instanceof Protector) {

            log.info("보호자 객체 검증 완료");
            return (Protector) user;
        }

        throw new CustomErrorException(ErrorType.AccessDeniedError);
    }

    private String validateFcmToken(FcmTokenSave.Request request){

        String fcmToken = request.getFcmToken();
        if(fcmToken == null){
            throw new CustomErrorException(ErrorType.NotValidRequestError);
        }

        return fcmToken;
    }
}
