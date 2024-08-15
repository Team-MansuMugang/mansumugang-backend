package org.mansumugang.mansumugang_service.config;


import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;


import java.io.FileInputStream;
import java.io.IOException;

@Slf4j
@Configuration
public class FirebaseConfig {


    /**
     * FCM 설정
     * ClassPathResource : Resource 폴더 이하 파일 경로 부터 읽음. -> 현재 비공개 엑세스 키 파일(Json) 경로 == resources/firebase/파일명.json
     */

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        try {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(
                            GoogleCredentials.fromStream(new ClassPathResource("/firebase/mansumugang-service-firebase-adminsdk-22kx1-d73706ff32.json").getInputStream())
                    )
                    .build();

            log.info("Fcm 설정 성공");
            return FirebaseApp.initializeApp(options);

        } catch (IOException exception) {
            log.error("Fcm 연결 오류 {}", exception.getMessage());
            return null;
        }
    }

    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }
}
