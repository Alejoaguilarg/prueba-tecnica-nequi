# ---------- BUILD ----------
FROM gradle:8.9-jdk21-alpine AS build
WORKDIR /workspace

COPY settings.gradle build.gradle main.gradle gradle/ ./
COPY settings.gradle build.gradle main.gradle gradle.properties gradle/ ./

# Copia módulos físicos del scaffold
COPY applications/ applications/
COPY domain/ domain/
COPY infrastructure/ infrastructure/

RUN gradle :app-service:bootJar --no-daemon

# ---------- RUNTIME ----------
FROM eclipse-temurin:21-jre-alpine
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"
WORKDIR /app
COPY --from=build /workspace/applications/app-service/build/libs/*.jar app.jar
EXPOSE 8080
USER 1001
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -Dspring.profiles.active=docker -jar app.jar"]
