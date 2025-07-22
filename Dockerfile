# =================================================================
# 1단계: 빌드(Build) 환경 (JDK 24 사용)
# Gradle과 JDK 24를 사용하여 Spring Boot 애플리케이션을 빌드합니다.
# =================================================================
FROM gradle:jdk24 AS builder

WORKDIR /app

# Gradle wrapper와 build.gradle 등 필요한 파일들을 먼저 복사
COPY backend/ZMNNOORY/gradle/ ./gradle/
COPY backend/ZMNNOORY/gradlew ./
COPY backend/ZMNNOORY/gradlew.bat ./
COPY backend/ZMNNOORY/build.gradle ./
COPY backend/ZMNNOORY/settings.gradle ./

# Gradle wrapper 실행 권한 부여
RUN chmod +x ./gradlew

# 의존성 다운로드 (캐시 활용을 위해)
RUN ./gradlew dependencies --no-daemon

# 소스 코드 복사
COPY backend/ZMNNOORY/src/ ./src/

# Spring Boot JAR 파일 빌드
RUN ./gradlew clean bootJar --no-daemon

# [디버깅] 빌드 결과물 확인
RUN ls -la /app/build/libs/

# =================================================================
# 2단계: 최종 실행(Runtime) 환경 (JDK 24 사용)
# Spring Boot는 내장 Tomcat을 포함하므로 전체 JDK 사용
# =================================================================
FROM openjdk:24-jdk

# 컨테이너 내 작업 디렉토리 설정
WORKDIR /app

# 애플리케이션 실행에 필요한 사용자 생성 (보안상 권장)
RUN groupadd -r spring && useradd -r -g spring spring

# 빌드된 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar /app/app.jar

# 파일 소유권을 spring 사용자로 변경
RUN chown spring:spring /app/app.jar

# spring 사용자로 실행
USER spring

# 애플리케이션이 사용할 포트 노출
EXPOSE 8080

# 환경변수 기본값 설정 (Kubernetes 환경용)
ENV SPRING_PROFILES_ACTIVE="k8s"
ENV SERVER_PORT="8080"
ENV JWT_SECRET_KEY="change-me-in-production-this-is-not-secure-enough-for-production-use"
ENV JWT_ISSUER="ZMNNOORY"
ENV JWT_EXPIRATION="3h"
ENV JWT_PREFIX="Bearer "
ENV JWT_HEADER="Authorization"

# JVM 최적화 옵션과 함께 애플리케이션 실행
ENTRYPOINT ["java", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", \
    "-Dserver.port=${SERVER_PORT}", \
    "-Xms512m", \
    "-Xmx1024m", \
    "-XX:+UseG1GC", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-jar", \
    "/app/app.jar"] 