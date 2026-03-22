# Multi-stage Dockerfile for OmniCharge Backend
# Memory-optimized build

FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

# Copy all pom files first
COPY DiscoveryServer/pom.xml ./DiscoveryServer/
COPY ConfigServer/pom.xml ./ConfigServer/
COPY ApiGateway/pom.xml ./ApiGateway/
COPY UserService/pom.xml ./UserService/

# Copy all source files
COPY DiscoveryServer/src ./DiscoveryServer/src
COPY ConfigServer/src ./ConfigServer/src
COPY ApiGateway/src ./ApiGateway/src
COPY UserService/src ./UserService/src

# Build DiscoveryServer (lightest)
WORKDIR /build/DiscoveryServer
RUN mvn dependency:go-offline -B && \
    mvn clean package -DskipTests -B -o 2>/dev/null || mvn clean package -DskipTests -B

# Build ConfigServer
WORKDIR /build/ConfigServer
RUN mvn dependency:go-offline -B && \
    mvn clean package -DskipTests -B -o 2>/dev/null || mvn clean package -DskipTests -B

# Build ApiGateway
WORKDIR /build/ApiGateway
RUN mvn dependency:go-offline -B && \
    mvn clean package -DskipTests -B -o 2>/dev/null || mvn clean package -DskipTests -B

# Build UserService
WORKDIR /build/UserService
RUN mvn dependency:go-offline -B && \
    mvn clean package -DskipTests -B -o 2>/dev/null || mvn clean package -DskipTests -B

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

RUN apk add --no-cache \
    bash \
    curl \
    supervisor \
    openssl \
    netcat-openbsd

RUN mkdir -p /app/services /app/logs /app/supervisor

COPY --from=builder /build/DiscoveryServer/target/*.jar /app/services/
COPY --from=builder /build/ConfigServer/target/*.jar /app/services/
COPY --from=builder /build/ApiGateway/target/*.jar /app/services/
COPY --from=builder /build/UserService/target/*.jar /app/services/

COPY startup.sh /app/
COPY supervisord.conf /app/supervisor/

RUN chmod +x /app/startup.sh

ENV DB_HOST=localhost
ENV DB_PORT=5432
ENV DB_NAME=omnicharge
ENV DB_USER=postgres
ENV DB_PASSWORD=

EXPOSE 8080 8761 8888 8081

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

CMD ["/app/startup.sh"]
