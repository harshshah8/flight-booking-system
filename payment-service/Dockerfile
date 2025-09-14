FROM openjdk:21-jdk-slim

WORKDIR /app

# Copy the JAR file
COPY target/payment-service-0.0.1-SNAPSHOT.jar payment-service.jar

# Expose the port
EXPOSE 8084

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:8084/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "payment-service.jar"]