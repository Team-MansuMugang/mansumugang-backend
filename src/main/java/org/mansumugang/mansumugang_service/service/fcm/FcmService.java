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
import org.mansumugang.mansumugang_service.service.user.UserCommonService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {

    private final FirebaseMessaging firebaseMessaging;
    private final FcmTokenRepository fcmTokenRepository;

    private final UserCommonService userCommonService;

    @Transactional
    public void saveFcmToken(User user, FcmTokenSave.Request request){

        Protector validProtector = userCommonService.findProtector(user);

        String validFcmToken = validateFcmToken(request);

        fcmTokenRepository.save(FcmToken.of(validFcmToken, validProtector));

    }

    public void sendMessage(Message message){

        try {
            String response = firebaseMessaging.send(message);
            log.info(response);
        } catch (FirebaseMessagingException e) {
            log.error(e.getMessage());

        }
    }


    private String validateFcmToken(FcmTokenSave.Request request){

        String fcmToken = request.getFcmToken();
        if(fcmToken == null){
            throw new CustomErrorException(ErrorType.NotValidRequestError);
        }

        return fcmToken;
    }
}
