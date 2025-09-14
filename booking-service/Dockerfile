FROM openjdk:21-jdk-slim

WORKDIR /app

# Copy the JAR file
COPY target/booking-service-0.0.1-SNAPSHOT.jar booking-service.jar

# Expose the port
EXPOSE 8083

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:8083/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "booking-service.jar"]