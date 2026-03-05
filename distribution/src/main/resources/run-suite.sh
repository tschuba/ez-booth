#!/bin/bash
# Absoluter Pfad zum Suite-Verzeichnis
BASEDIR=$(cd "$(dirname "$0")" && pwd)

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
"$BASEDIR/apps/ez-booth-vaadin-ui.app/Contents/MacOS/ez-booth-vaadin-ui" > "$BASEDIR/logs/ui.log" 2>&1

# Aufräumen beim Beenden
echo "👋 Shutting down..."
kill $SERVER_PID 2>/dev/null