#!/bin/bash
set -e

echo "========================================="
echo "OmniCharge Backend - Starting Services"
echo "========================================="

DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-5432}"
DB_NAME="${DB_NAME:-omnicharge}"
DB_USER="${DB_USER:-postgres}"
DB_PASSWORD="${DB_PASSWORD:-}"

wait_for_db() {
  echo "Waiting for PostgreSQL at $DB_HOST:$DB_PORT..."
  local max_attempts=30
  local attempt=1

  while [ $attempt -le $max_attempts ]; do
    if nc -z -w 2 "$DB_HOST" "$DB_PORT" 2>/dev/null; then
      echo "PostgreSQL is available!"
      return 0
    fi
    echo "Attempt $attempt/$max_attempts - Database not ready, waiting..."
    sleep 2
    attempt=$((attempt + 1))
  done

  echo "WARNING: Database not available after $max_attempts attempts, continuing..."
  return 0
}

wait_for_eureka() {
  echo "Waiting for Discovery Server at localhost:8761..."
  local max_attempts=40 # 40 x 3s = 2 minutes
  local attempt=1

  while [ $attempt -le $max_attempts ]; do
    if curl -s -f http://localhost:8761/actuator/health >/dev/null 2>&1; then
      echo "Discovery Server is UP!"
      return 0
    fi
    echo "Attempt $attempt/$max_attempts - Discovery Server not ready, waiting..."
    sleep 3
    attempt=$((attempt + 1))
  done

  echo "WARNING: Discovery Server not available after $max_attempts attempts, continuing..."
  return 0
}

wait_for_config() {
  echo "Waiting for Config Server at localhost:8888..."
  local max_attempts=40
  local attempt=1

  while [ $attempt -le $max_attempts ]; do
    if curl -s -f http://localhost:8888/actuator/health >/dev/null 2>&1; then
      echo "Config Server is UP!"
      return 0
    fi
    echo "Attempt $attempt/$max_attempts - Config Server not ready, waiting..."
    sleep 3
    attempt=$((attempt + 1))
  done

  echo "WARNING: Config Server not available after $max_attempts attempts, continuing..."
  return 0
}

start_discovery() {
  echo "Starting Discovery Server..."
  java ${JAVA_OPTS} -jar /app/services/DiscoveryServer-1.0.jar \
    >/proc/1/fd/1 2>/proc/1/fd/2 &
  wait_for_eureka
}

start_config() {
  echo "Starting Config Server..."
  java ${JAVA_OPTS} -jar /app/services/ConfigServer-1.0.jar \
    >/proc/1/fd/1 2>/proc/1/fd/2 &
  wait_for_config
}

start_remaining_services() {
  echo "Starting remaining services via supervisord..."

  export SPRING_DATASOURCE_URL="jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}"
  export SPRING_DATASOURCE_USERNAME="${DB_USER}"
  export SPRING_DATASOURCE_PASSWORD="${DB_PASSWORD}"

  exec /usr/bin/supervisord -c /app/supervisor/supervisord.conf
}

main() {
  mkdir -p /app/logs

  # Fix for Eureka hostname resolution (all services in same container)
  echo "127.0.0.1 discovery-server" >> /etc/hosts

  export SPRING_DATASOURCE_URL="jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}"
  export SPRING_DATASOURCE_USERNAME="${DB_USER}"
  export SPRING_DATASOURCE_PASSWORD="${DB_PASSWORD}"

  # Step 1: Wait for external DB
  wait_for_db

  # Step 2: Start Eureka first, wait for it to be healthy
  start_discovery

  # Step 3: Start Config Server, wait for it to be healthy
  start_config

  # Step 4: Now start everything else via supervisord
  start_remaining_services
}

main "$@"
