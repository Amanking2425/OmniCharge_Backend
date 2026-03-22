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

  echo "WARNING: Database not available after $max_attempts attempts"
  echo "Continuing anyway - services may retry..."
  return 0
}

wait_for_eureka() {
  echo "Waiting for Discovery Server at localhost:8761..."
  local max_attempts=5
  local attempt=1

  while [ $attempt -le $max_attempts ]; do
    if curl -s -f http://localhost:8761/actuator/health >/dev/null 2>&1; then
      echo "Discovery Server is available!"
      return 0
    fi
    echo "Attempt $attempt/$max_attempts - Discovery Server not ready..."
    sleep 2
    attempt=$((attempt + 1))
  done

  echo "WARNING: Discovery Server not available after $max_attempts attempts"
  echo "Continuing anyway - services will start and retry..."
  return 0
}

configure_and_start() {
  echo "Starting services..."

  mkdir -p /app/logs

  export SPRING_DATASOURCE_URL="jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}"
  export SPRING_DATASOURCE_USERNAME="${DB_USER}"
  export SPRING_DATASOURCE_PASSWORD="${DB_PASSWORD}"
  echo "DB URL: $SPRING_DATASOURCE_URL"
  
  echo "Starting supervisord..."
  exec /usr/bin/supervisord -c /app/supervisor/supervisord.conf
}

main() {
  wait_for_db
  wait_for_eureka
  configure_and_start
}

main "$@"
