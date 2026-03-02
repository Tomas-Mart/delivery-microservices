# syntax=docker/dockerfile:1.4
ARG SERVICE_NAME

# Build stage
FROM maven:3.8-openjdk-17 AS builder
WORKDIR /build

# Копируем POM файлы для кэширования зависимостей
COPY pom.xml .
COPY shared/pom.xml shared/
COPY ${SERVICE_NAME}/pom.xml ${SERVICE_NAME}/

# Скачиваем зависимости (кэшируется)
RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline -B

# Копируем исходники
COPY . .

# Собираем только нужный сервис
RUN --mount=type=cache,target=/root/.m2 \
    mvn clean package -pl ${SERVICE_NAME} -am -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Создаём непривилегированного пользователя
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Копируем JAR из builder stage
COPY --from=builder --chown=appuser:appgroup /build/${SERVICE_NAME}/target/*.jar app.jar

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD java -jar app.jar --health || exit 1

# Метки для лучшего управления образами
LABEL maintainer="Ksenia Tomas-Mart" \
      org.opencontainers.image.source="https://gitlab.com/kxsenia/delivery-microservices" \
      org.opencontainers.image.description="Delivery microservice" \
      org.opencontainers.image.licenses="MIT"

USER appuser
EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]