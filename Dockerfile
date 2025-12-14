FROM openjdk:17

ENV SPRING_PROFILES_ACTIVE=docker

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from the builder stage
COPY /target/S1Service-0.0.1-SNAPSHOT.jar /app/S1Service-v-0.0.1.jar

# Copy SSL certificate and key
COPY cert.pem /app/cert.pem
COPY key.pem /app/key.pem
COPY keystore.p12 /app/keystore.p12

# Copy FreeMarker templates
COPY src/main/resources/templates/ /app/src/main/resources/templates/

# Expose the application port
EXPOSE 4444

# Command to run the application
ENTRYPOINT ["java", "-jar", "S1Service-v-0.0.1.jar"]
