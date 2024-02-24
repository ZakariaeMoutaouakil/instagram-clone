# Define build arguments for database connection details
ARG DB_URL
ARG DB_USERNAME
ARG DB_PASSWORD
ARG DEBUG

# Build stage
FROM maven:3.9.6-eclipse-temurin-21-jammy AS build
WORKDIR /home/app

# Copy only the necessary files for dependency resolution
COPY ./pom.xml /home/app/pom.xml
RUN mvn -f /home/app/pom.xml dependency:go-offline

# Copy the application source code and package it
COPY ./src /home/app/src
RUN mvn -f /home/app/pom.xml package -DskipTests

# Final stage
FROM openjdk:21-jdk-slim-buster
ARG DB_URL
ARG DB_USERNAME
ARG DB_PASSWORD
ARG DEBUG
EXPOSE 8080

# Copy the built JAR file from the previous stage
COPY --from=build /home/app/target/*.jar app.jar

# Set environment variables for runtime configuration
ENV JAVA_OPTS="-Xmx500m -Xms500m"
ENV DB_URL=${DB_URL}
ENV DB_USERNAME=${DB_USERNAME}
ENV DB_PASSWORD=${DB_PASSWORD}
ENV DEBUG=${DEBUG}

# Start the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app.jar"]
