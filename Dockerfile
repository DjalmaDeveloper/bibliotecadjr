# ================================
# Multi-Stage Dockerfile
# Sistema Biblioteca - Spring Boot Application
# ================================

# ================================
# Stage 1: Build
# ================================
FROM maven:3.9.5-eclipse-temurin-17-alpine AS builder

# Set working directory
WORKDIR /app

# Install curl for retry logic
RUN apk add --no-cache curl

# Copy Maven wrapper and pom.xml first (for better layer caching)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make mvnw executable
RUN chmod +x mvnw

# Configure Maven settings for better resilience
RUN mkdir -p /root/.m2 && \
    echo '<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" \
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" \
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 \
          https://maven.apache.org/xsd/settings-1.0.0.xsd"> \
          <mirrors> \
            <mirror> \
              <id>maven-default-http-blocker</id> \
              <mirrorOf>external:http:*</mirrorOf> \
              <name>Pseudo repository to mirror external repositories initially using HTTP.</name> \
              <url>http://0.0.0.0/</url> \
              <blocked>true</blocked> \
            </mirror> \
          </mirrors> \
        </settings>' > /root/.m2/settings.xml

# Download dependencies with retry logic (cached if pom.xml doesn't change)
RUN ./mvnw dependency:go-offline -B || \
    (echo "Retry 1/3..." && sleep 10 && ./mvnw dependency:go-offline -B) || \
    (echo "Retry 2/3..." && sleep 20 && ./mvnw dependency:go-offline -B) || \
    (echo "Retry 3/3..." && sleep 30 && ./mvnw dependency:go-offline -B)

# Copy source code
COPY src ./src

# Build application with retry logic (skip tests for faster builds)
RUN ./mvnw clean package -DskipTests -B || \
    (echo "Build retry 1/2..." && sleep 10 && ./mvnw clean package -DskipTests -B) || \
    (echo "Build retry 2/2..." && sleep 20 && ./mvnw clean package -DskipTests -B)

# Verify JAR was created
RUN ls -la target/ && \
    test -f target/*.jar && \
    echo "JAR file created successfully"

# ================================
# Stage 2: Runtime
# ================================
FROM eclipse-temurin:17-jre-alpine

# Install dumb-init for proper signal handling
RUN apk add --no-cache dumb-init

# Create non-root user for security
RUN addgroup -g 1001 spring && \
    adduser -u 1001 -G spring -s /bin/sh -D spring

# Set working directory
WORKDIR /app

# Copy jar from builder stage
COPY --from=builder --chown=spring:spring /app/target/*.jar app.jar

# Switch to non-root user
USER spring:spring

# Expose application port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/autores || exit 1

# Set JVM options for container environment
ENV JAVA_OPTS="-XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -XX:InitialRAMPercentage=50.0 \
    -Djava.security.egd=file:/dev/./urandom \
    -Djava.net.preferIPv4Stack=true"

# Use dumb-init to handle signals properly
ENTRYPOINT ["dumb-init", "--"]

# Run the application
# Note: PORT environment variable is set by Render
CMD sh -c "java $JAVA_OPTS -Dserver.port=${PORT:-8080} -Dspring.profiles.active=prod -jar app.jar"
