#!/bin/bash

# Default values
BACKEND_PORT=8080
JAR_VERSION=1.0.0
TIMEOUT=60 # seconds

# Parse named options
while [[ $# -gt 0 ]]; do
  case $1 in
    --port)
      BACKEND_PORT="$2"
      shift 2
      ;;
    --version)
      JAR_VERSION="$2"
      shift 2
      ;;
    *)
      echo "Unknown option $1"
      echo "Usage: $0 [--port <port>] [--version <jar-version>]"
      exit 1
      ;;
  esac
done

# Cleanup function to stop backend and frontend
cleanup() {
  echo "Stopping applications..."
  if [ -n "$FRONTEND_PID" ] && ps -p $FRONTEND_PID > /dev/null; then
    kill $FRONTEND_PID
    echo "Frontend stopped."
  fi
  if [ -n "$BACKEND_PID" ] && ps -p $BACKEND_PID > /dev/null; then
    kill $BACKEND_PID
    echo "Backend stopped."
  fi
  exit 0
}

trap cleanup SIGINT SIGTERM EXIT

echo "Starting backend ez-booth-server (version $JAR_VERSION) on port $BACKEND_PORT..."
java -jar ez-booth-server-$JAR_VERSION.jar &
BACKEND_PID=$!

sleep 1
if ! ps -p $BACKEND_PID > /dev/null; then
  echo "ERROR: Backend process failed to start. Check ez-booth-server-$JAR_VERSION.jar and Java installation."
  exit 1
fi

echo "Checking if backend is ready (timeout: $TIMEOUT seconds)..."
SECONDS=0
until curl -sf http://localhost:$BACKEND_PORT/actuator/health > /dev/null; do
  if ! ps -p $BACKEND_PID > /dev/null; then
    echo "ERROR: Backend process terminated unexpectedly."
    exit 1
  fi
  if (( SECONDS > TIMEOUT )); then
    echo "ERROR: Backend did not become ready within $TIMEOUT seconds."
    kill $BACKEND_PID 2>/dev/null
    exit 1
  fi
  echo "Waiting for backend ez-booth-server on port $BACKEND_PORT..."
  sleep 2
done

echo "Backend is ready! Starting frontend (version $JAR_VERSION)..."
java -jar ez-booth-vaadin-ui-$JAR_VERSION.jar &
FRONTEND_PID=$!

wait $BACKEND_PID
wait $FRONTEND_PID