# Root Dockerfile for XRPShield Spring Boot Backend
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY backend/pom.xml backend/
COPY backend/src backend/src
WORKDIR /app/backend
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/backend/target/xrpshield-backend-1.0.0-SNAPSHOT.jar app.jar
ENV PORT=8080
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=8080"]
