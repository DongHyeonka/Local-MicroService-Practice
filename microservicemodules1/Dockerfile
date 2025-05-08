# syntax=docker/dockerfile:1

# =========================================
# 빌드 스테이지 (Builder Stage)
# =========================================
FROM eclipse-temurin:21-jdk-alpine AS builder

# 작업 디렉토리 설정
WORKDIR /app

# Gradle 빌드를 위한 의존성 먼저 복사
COPY build.gradle settings.gradle gradlew ./
COPY gradle gradle

# 의존성 다운로드 (캐시 활용)
RUN chmod +x ./gradlew && ./gradlew dependencies --no-daemon

# 소스 코드 복사
COPY src ./src

# 애플리케이션 빌드
RUN ./gradlew bootJar --no-daemon

# JAR 파일 위치 확인
RUN mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/kubernetesmodules1-0.0.1-SNAPSHOT.jar)

# =========================================
# 런타임 스테이지 (Runtime Stage)
# =========================================
FROM eclipse-temurin:21-jre-alpine

# --- Pinpoint Agent 추가 시작 ---

# 필요한 유틸리티 설치 (tar)
RUN apk add --no-cache tar

# Pinpoint Agent 파일 복사 및 압축 해제
COPY pinpoint-agent-*.tar.gz /tmp/pinpoint-agent.tar.gz
RUN mkdir -p /app/pinpoint-agent && \
    tar -xzf /tmp/pinpoint-agent.tar.gz -C /app/pinpoint-agent --strip-components=1 && \
    rm /tmp/pinpoint-agent.tar.gz
# --- Pinpoint Agent 추가 끝 ---

# 보안을 위한 비루트 사용자 설정
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# 볼륨 포인트 생성 (로그, 설정 등을 위해)
VOLUME /tmp

# 작업 디렉토리 설정
WORKDIR /app

# 환경 변수 설정
ENV SPRING_PROFILES_ACTIVE=production
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

# 애플리케이션 레이어 복사
ARG DEPENDENCY=/app/build/dependency
COPY --from=builder ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=builder ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=builder ${DEPENDENCY}/BOOT-INF/classes /app

# --- Pinpoint Agent 권한 설정 ---
# Agent 디렉토리 소유권 변경
RUN chown -R appuser:appgroup /app/pinpoint-agent
# --- Pinpoint Agent 권한 설정 끝 ---

# 5000 포트 노출
EXPOSE 5000

# 비루트 사용자로 전환
USER appuser

# 헬스체크를 위한 라벨 추가
LABEL maintainer="Your Organization <email@example.com>"
LABEL version="1.0"
LABEL description="Spring Boot API Gateway for microservices architecture"

# 애플리케이션 실행
ENTRYPOINT ["java", "-cp", "/app:/app/lib/*", "com.springboot.kubernetesmodules1.Kubernetesmodules1Application"]
