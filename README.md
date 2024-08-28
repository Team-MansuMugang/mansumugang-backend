![Logo](https://github.com/user-attachments/assets/c6071a1f-6765-49b6-b293-3980e298e25b)
# 만수무강 백엔드

__고령의 환자와 보호자 모두가 안심할 수 있는__ 공간을 만들기 위한 안심케어 및 커뮤니티 서비스 __만수무강 입니다.__

## 🌟 서비스 기능

- __약 일정 관리 및 기록__: 약 복용 스케줄 설정 및 복약 기록을 통해 케어 맴버가 약을 놓치지 않고 복용할 수 있도록 지원합니다.
- __병원 일정 관리 및 기록__: 케어 맴버의 병원 예약 일정 관리와 방문 기록을 한눈에 확인할 수 있는 기능을 제공합니다.
- __음성 메시지 기능__: 케어 맴버와 음성 메시지를 통해 편리한 커뮤니케이션 할 수 있는 도구입니다.
- __음성 메시지 텍스트 변환 (ASR)__: 녹음된 음성을 자동으로 텍스트로 변환하여 편리성을 높입니다.
- __건강 커뮤니티__: 다양한 건강 관련 주제에 대해 다른 사용자들과 정보를 공유하고 소통할 수 있는 커뮤니티 기능입니다.
- __사용자 계정 관리__: 보호자와 케어 멤버를 위한 손쉬운 회원가입, 계정 관리 및 탈퇴 기능을 제공합니다.

## 🛠️  기술 스택
- __Java 17__
- __Spring Boot 3.3.1__
- __Gradle__
- __MySQL__
- __Redis__
- __JPA__
- __Whisper__
- __FCM__
- __AWS__

## ✅ 사전 요구사항
이 프로젝트를 실행하기 위해서는 아래의 환경이 필요합니다:

### 사전 설치
- Java 17
- Gradle
- MySQL
- Redis
- AWS S3 사용을 위한 AccessKey 발급

### 외부 API
- Whisper를 통한 STT 기능을 사용하기 위한 OpenAI사의 API 토근
- FCM 기능을 사용하기 위한 Firebase Project 생성
- AWS S3 사용을 위한 AccessKey 발급

## 🖥️  어플리케이션 실행
### 1. 프로젝트 Clone
```
git clone https://github.com/Team-MansuMugang/mansumugang-backend.git
```

### 2. 프로젝트 이동
```
cd mansumugang-backend
```

### 3. application.yaml 작성

다음 3개의 파일을 `src/main/resources/static` 아래에 붙여 넣습니다.
```
# application.yaml

spring:
  profiles:
    group:
      local: local-profile, common
      dev: dev-profile, common
      prod: prod-profile, common

server:
  env: blue
```

</br>
</br>

AWS의 S3 버킷이름, 리전, AccessToken, OpenAI API key를 기입해줍니다.
```
# application-common.yaml

spring:
  config:
    activate:
      on-profile: common
  servlet:
    multipart:
      maxFileSize: 100MB
      maxRequestSize: 300MB

  jackson:
    default-property-inclusion: NON_NULL
    parser:
      allow-unquoted-control-chars: true

application:
  name:

cloud:
  aws:
    s3:
      bucket: #####
    stack.auto: false
    region.static: #####
    credentials:
      accessKey: #####
      secretKey: #####

  port: 8080
  servlet:
    context-path: /

openai-service:
  api-key: #####
  gpt-model: gpt-3.5-turbo
  audio-model: whisper-1
  http-client:
    read-timeout: 3000
    connect-timeout: 3000
  urls:
    base-url: https://api.openai.com/v1
    chat-url: /chat/completions
    create-transcription-url: /audio/transcriptions

management:
  endpoints:
    web:
      base-path: /actuator
      exposure:
        include: '*'
  endpoint:
    health:
      show-details: 'ALWAYS'
  health:
    circuitbreakers:
      enabled: true
```

</br>
MySQL 루트 비밀번호와 JWT Secret Key를 입력합니다.

```
application-local.yaml

spring:
  config:
    activate:
      on-profile: local-profile
  data:
    redis:
      host: localhost
      port: 6379
  datasource:
    url: jdbc:mysql://localhost:3306/mansumugang_service
    username: root
    password: #####
    driver-class-name: com.mysql.cj.jdbc.Driver

  # JPA Configuration
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect

jwt:
  secret: #####
  access:
    expiration: 86400000
    header: Authorization

  refresh:
    expiration: 1209600000
    header: Authorization-refresh

server:
  port: 8080
serverName: local-server

logging:
  level:
    root: info

file:
  upload:
    image:
      api: http://localhost:8080/images/
      path: src/main/resources/static/images
    audio:
      api: http://localhost:8080/audios/
      path: src/main/resources/static/audios
    postImages:
      api: http://localhost:8080/postImages/
      path: src/main/resources/static/postImages
```

</br>
</br>

### 4. firebase 비공개 키 기입

- firebase 프로젝트 설정 > 서비스 계정 > 비공개 키 생성을 클릭하여 비공개 생성합니다.
- `src/main/resources/firebase` 폴더를 생성하고 비공개 키 파일 이름을 `mansumugang-service-firebase-adminsdk-22kx1-d73706ff32.json`으로 변경하여 폴더 아래에 붙여 넣습니다.

### 5. 정적 파일 저장을 위한 폴더 생성
- `src/main/resources/static` 아래에 `images`, `audios`, `postImages` 이름의 3개의 폴더를 생성합니다.

### 6. 프로젝트 빌드
```
./gradlew build -x test
```

### 7. 프로젝트 실행
```
java -Duser.timezone=Asia/Seoul -Dspring.profiles.active=local -jar ./build/libs/mansumugang-service-0.0.1-SNAPSHOT.jar
```

## 🎥 데모 영상

[![Video Label](http://img.youtube.com/vi/6WP0Y-pIVe8/0.jpg)](https://youtu.be/6WP0Y-pIVe8?si=HEJ8IRgIGGOGIK5Q)

## 🧑‍💻 개발자

- [@JSH0905](https://github.com/JSH0905)
- [@minnnisu](https://github.com/minnnisu)
