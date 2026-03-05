#!/bin/bash
# Ermittle das Verzeichnis, in dem dieses Skript liegt
BASEDIR=$(dirname "$0")

echo "🚀 Starting ez-booth Suite..."

# Server im Hintergrund starten (nutzt die Shared Runtime)
# Wir gehen davon aus, dass das Skript in /apps oder im Root liegt
"$BASEDIR/apps/ez-booth-server.app/Contents/MacOS/ez-booth-server" &
SERVER_PID=$!

# Kurz warten, bis der Server (gRPC) bereit ist
sleep 2

# UI starten
"$BASEDIR/apps/ez-booth-vaadin-ui.app/Contents/MacOS/ez-booth-vaadin-ui"

# Wenn UI beendet wird, auch Server stoppen
kill $SERVER_PID