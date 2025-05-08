# syntax=docker/dockerfile:1

# 빌드 스테이지: 애플리케이션을 빌드하는 단계
FROM eclipse-temurin:21-jdk-alpine AS builder

# 작업 디렉토리 설정
WORKDIR /app

# Gradle 래퍼 및 빌드 파일 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 실행 권한 부여
RUN chmod +x ./gradlew

# 의존성 다운로드 (캐시 활용)
RUN ./gradlew dependencies --no-daemon

# 소스 코드 복사
COPY src ./src

# 애플리케이션 빌드
RUN ./gradlew build -x test --no-daemon

# 빌드된 JAR 파일 압축 해제
RUN mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/kubernetesmodules3-0.0.1-SNAPSHOT.jar)

# 런타임 스테이지: 실제 프로덕션 이미지를 위한 단계
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

# 보안을 위한 비루트 사용자 설정 (일관성을 위해 appuser/appgroup 사용)
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# 볼륨 포인트 생성 (로그, 설정 등을 위해)
VOLUME /tmp

# 작업 디렉토리 설정
WORKDIR /app

# 환경 변수 설정
ENV SPRING_PROFILES_ACTIVE=production
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

# 빌더 스테이지에서 생성된 애플리케이션 레이어 복사 (Exploded 방식)
ARG DEPENDENCY=/app/build/dependency
COPY --from=builder ${DEPENDENCY}/BOOT-INF/lib /app/BOOT-INF/lib
COPY --from=builder ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=builder ${DEPENDENCY}/BOOT-INF/classes /app/BOOT-INF/classes

# --- Pinpoint Agent 권한 설정 ---
# Agent 디렉토리 소유권 변경
RUN chown -R appuser:appgroup /app/pinpoint-agent
# --- Pinpoint Agent 권한 설정 끝 ---

# ===> 포트 변경: 노출할 포트
EXPOSE 5002

# 비루트 사용자로 전환
USER appuser

# 메타데이터 라벨 추가
LABEL maintainer="Your Organization <email@example.com>"
LABEL version="1.0"
LABEL description="Microservice kubernetesmodule3 for the Kubernetes architecture"

# 애플리케이션 실행 (java -cp 방식)
ENTRYPOINT ["java", "-cp", "/app/BOOT-INF/classes:/app/BOOT-INF/lib/*", "com.springboot.kubernetesmodules3.Kubernetesmodules3Application"]
