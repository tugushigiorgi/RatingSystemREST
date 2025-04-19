# Use Eclipse Temurin (OpenJDK) as base image
FROM eclipse-temurin:21-jdk-alpine

# Set the working directory
WORKDIR /app

# Add the executable jar to the container
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Expose the port your Spring Boot app runs on
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
