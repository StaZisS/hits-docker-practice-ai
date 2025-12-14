#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

cd "$ROOT_DIR"

./mvnw -q -DskipTests package

JAR_PATH="$(ls -1 target/*.jar | head -n 1)"

SPRING_PROFILES_ACTIVE=local java -jar "$JAR_PATH" > /tmp/shopapi-local.log 2>&1 &
APP_PID=$!

cleanup() {
  kill "$APP_PID" >/dev/null 2>&1 || true
}
trap cleanup EXIT

echo "Waiting for app to start (pid=$APP_PID)..."
for i in {1..60}; do
  if curl -sf http://localhost:8080/api/analytics/db >/dev/null; then
    break
  fi
  sleep 1
done

mkdir -p loadtest/out
javac -d loadtest/out loadtest/LoadTest.java

java -cp loadtest/out LoadTest --base-url http://localhost:8080 --concurrency "${CONCURRENCY:-30}" --duration-seconds "${DURATION_SECONDS:-30}" --seed "${SEED:-100}"

