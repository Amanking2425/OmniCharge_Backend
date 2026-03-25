FROM eclipse-temurin:21-jre-alpine

RUN apk add --no-cache \
    bash \
    curl \
    supervisor \
    openssl \
    netcat-openbsd

RUN mkdir -p /app/services /app/logs /app/supervisor

COPY DiscoveryServer/target/*.jar /app/services/
COPY ConfigServer/target/*.jar /app/services/
COPY ApiGateway/target/*.jar /app/services/
COPY UserService/target/*.jar /app/services/
COPY NotificationService/target/*.jar /app/services/
COPY PaymentService/target/*.jar /app/services/
COPY RechargeService/target/*.jar /app/services/
COPY OperatoraService/target/*.jar /app/services/

COPY startup.sh /app/
COPY supervisord.conf /app/supervisor/

RUN chmod +x /app/startup.sh

ENV DB_HOST=localhost
ENV DB_PORT=5432
ENV DB_NAME=omnicharge
ENV DB_USER=postgres
ENV DB_PASSWORD=
ENV JAVA_OPTS="-Xms64m -Xmx180m -XX:+UseSerialGC -XX:MaxMetaspaceSize=80m"

EXPOSE 8080 8081 8082 8083 8084 8085 8086 8087 8761 8888

HEALTHCHECK --interval=30s --timeout=10s --start-period=120s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

CMD ["/app/startup.sh"]
