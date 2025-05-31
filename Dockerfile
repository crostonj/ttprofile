FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copy Maven project files and source
COPY pom.xml .
COPY src ./src

# Build the app
RUN mvn clean package

# Use a Java 21 runtime base image (not Java 17)
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/Profile-1.0.jar app.jar
COPY src/main/resources/application.yaml /app/application.yaml

EXPOSE 8082

ENV SPRING_CONFIG_LOCATION=file:/app/application.yaml

CMD ["java", "-jar", "app.jar"]

