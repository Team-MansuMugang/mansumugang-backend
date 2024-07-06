FROM openjdk:17-jdk-slim

WORKDIR /app

ARG JAR_FILE=./build/libs/mansumugang-service-0.0.1-SNAPSHOT.jar
ARG PROFILES
ARG ENV

# JAR 파일 메인 디렉토리에 복사
COPY ${JAR_FILE} deployment-study.jar

# 시스템 진입점 정의
ENTRYPOINT ["java", "-Dspring.profiles.active=${PROFILES}", "-Dserver.env=${ENV}", "-jar", "deployment-study.jar"]