# ---- Build stage ----
FROM gradle:8.10.2-jdk21 AS build
WORKDIR /workspace
COPY settings.gradle build.gradle gradle/wrapper/gradle-wrapper.properties ./
COPY backend/src ./backend/src
WORKDIR /workspace/backend
RUN gradle build -x test --no-daemon || gradle build -x test --offline --no-daemon

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/backend/build/libs/*.jar app.jar
EXPOSE 8080
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
