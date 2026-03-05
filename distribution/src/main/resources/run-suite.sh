#!/bin/bash

# Absoluter Pfad zum Suite-Verzeichnis
BASEDIR=$(cd "$(dirname "$0")" && pwd)
UI_PORT=8090
SERVER_PORT=8091

# Automatisch alte Prozesse auf den Ports finden und beenden
for PORT in $SERVER_PORT $UI_PORT; do
    PID=$(lsof -ti :$PORT)
    if [ ! -z "$PID" ]; then
        echo "  -> Closing old process on port $PORT (PID: $PID)..."
        kill -9 $PID 2>/dev/null
    fi
done

echo "🚀 Starting ez-booth Suite..."

# Log-Verzeichnis sicherstellen
mkdir -p "$BASEDIR/logs"

# Vorherige Logs aufräumen (optional)
echo "--- New Session $(date) ---" >> "$BASEDIR/logs/server.log"

echo "  -> Starting Server..."
"$BASEDIR/apps/ez-booth-server.app/Contents/MacOS/ez-booth-server" > "$BASEDIR/logs/server.log" 2>&1 &
SERVER_PID=$!

# Kurze Pause zur Initialisierung
sleep 2

# Prüfen, ob der Server noch läuft
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

# Wenn das UI-Fenster/Prozess geschlossen wird:
echo "👋 UI closed. Shutting down server..."
kill $SERVER_PID 2>/dev/null