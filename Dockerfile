# Use an official OpenJDK runtime as a parent image
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy the Gradle wrapper and build files
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle.kts build.gradle.kts
COPY settings.gradle.kts settings.gradle.kts
COPY src src

# Grant execute permission to the Gradle wrapper
RUN chmod +x gradlew

# Build the Spring application
RUN ./gradlew build --no-daemon

# Copy the built JAR file into the container
RUN cp build/libs/task-scheduler.jar task-scheduler.jar

# Expose the port your app runs on
EXPOSE 0000

# Command to run the Spring application
CMD ["java", "-jar", "task-scheduler.jar"]
