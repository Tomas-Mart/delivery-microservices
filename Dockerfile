# syntax=docker/dockerfile:1.4
ARG SERVICE_NAME
ARG SERVICE_PORT=8081

# Build stage
FROM maven:3.8-openjdk-17 AS builder
WORKDIR /build

# Копируем родительский POM
COPY pom.xml .

# Копируем POM-файлы модулей
COPY ${SERVICE_NAME}/pom.xml ${SERVICE_NAME}/pom.xml
COPY shared/pom.xml shared/pom.xml

# Скачиваем зависимости (кэшируется)
RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline -B

# Копируем исходники
COPY ${SERVICE_NAME}/src ${SERVICE_NAME}/src
COPY shared/src shared/src

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

# Метки
LABEL maintainer="Ksenia Tomas-Mart" \
      org.opencontainers.image.source="https://gitlab.com/kxsenia/delivery-microservices" \
      org.opencontainers.image.description="Delivery microservice" \
      org.opencontainers.image.licenses="MIT"

USER appuser
EXPOSE ${SERVICE_PORT}

ENTRYPOINT ["java", "-jar", "app.jar"]