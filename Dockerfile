# syntax=docker/dockerfile:1
#
# Run (pass all config via env file):
#   docker run -p 8080:8080 --env-file .env.prod your-image
# application.yaml only maps environment variables; real values live in .env.dev / .env.prod.

# --- Build ---
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

COPY gradlew settings.gradle build.gradle ./
COPY gradle gradle
RUN chmod +x gradlew

COPY src src

RUN ./gradlew bootJar --no-daemon -x test \
    && JAR="$(ls build/libs/*.jar | grep -v plain | head -1)" \
    && test -n "$JAR" \
    && cp "$JAR" /app/application.jar

# --- Runtime ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S appgroup -g 10001 \
    && adduser -S appuser -u 10001 -G appgroup

COPY --from=builder /app/application.jar /app/app.jar

RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 8080

ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
