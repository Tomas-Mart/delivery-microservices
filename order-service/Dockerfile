FROM dockerhub.timeweb.cloud/library/eclipse-temurin:17-jre-alpine
LABEL authors="Amina"

WORKDIR /app
COPY target/*.jar app.jar

EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]