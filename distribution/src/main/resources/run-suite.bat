@echo off
echo 🚀 Starting ez-booth Suite...

:: Server im Hintergrund starten (start /b verhindert ein blockierendes Fenster)
:: Wir nutzen .exe Pfade für Windows
start /b "" "%~dp0apps\ez-booth-server\ez-booth-server.exe"

:: Kurz warten (Timeout unter Windows)
timeout /t 2 /nobreak > nul

:: UI starten (Vordergrund)
echo Starting UI...
call "%~dp0apps\ez-booth-vaadin-ui\ez-booth-vaadin-ui.exe"

:: Hinweis: Das automatische Beenden des Hintergrund-Servers beim Schließen
:: der Batch ist unter Windows etwas komplexer, aber für den Anfang reicht das so.
echo UI closed. Please close the server window if it's still open.
pause