# Multi-stage build for optimized image size
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copy the JAR file
COPY target/Modoria-0.0.1-SNAPSHOT.jar app.jar

# Extract layers for better caching
RUN java -Djarmode=layertools -jar app.jar extract

# Final stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring

# Copy extracted layers
COPY --from=builder app/dependencies/ ./
COPY --from=builder app/spring-boot-loader/ ./
COPY --from=builder app/snapshot-dependencies/ ./
COPY --from=builder app/application/ ./

# Create logs directory
RUN mkdir -p /app/logs && chown -R spring:spring /app

# Switch to non-root user
USER spring:spring

EXPOSE 8081

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]