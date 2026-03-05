#!/bin/bash

# Absolute path to the suite directory
BASEDIR=$(cd "$(dirname "$0")" && pwd)
UI_PORT=8090
SERVER_PORT=8091

# Automatically find and stop old processes on the configured ports
for PORT in $SERVER_PORT $UI_PORT; do
    PID=$(lsof -ti :$PORT)
    if [ ! -z "$PID" ]; then
        echo "  -> Closing old process on port $PORT (PID: $PID)..."
        kill -9 $PID 2>/dev/null
    fi
done

echo "🚀 Starting ez-booth Suite..."

# Ensure log directory exists
mkdir -p "$BASEDIR/logs"

# Rotate previous logs (optional)
echo "--- New Session $(date) ---" >> "$BASEDIR/logs/server.log"

echo "  -> Starting Server..."
"$BASEDIR/apps/ez-booth-server.app/Contents/MacOS/ez-booth-server" > "$BASEDIR/logs/server.log" 2>&1 &
SERVER_PID=$!

# Short pause for initialization
sleep 2

# Check whether the server is still running
if ! kill -0 $SERVER_PID 2>/dev/null; then
    echo "❌ Server failed to start! Opening logs..."
    open "$BASEDIR/logs"
    exit 1
fi

echo "  -> Starting UI..."
echo "--------------------------------------------------"
echo "🌐 UI URL: http://localhost:$UI_PORT"
echo "--------------------------------------------------"

"$BASEDIR/apps/ez-booth-vaadin-ui.app/Contents/MacOS/ez-booth-vaadin-ui" \
  -Djdk.lang.Process.launchMechanism=FORK \
  > "$BASEDIR/logs/ui.log" 2>&1

# If the UI window/process is closed:
echo "👋 UI closed. Shutting down server..."
kill $SERVER_PID 2>/dev/null