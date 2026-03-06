@echo off
setlocal EnableExtensions EnableDelayedExpansion

:: --- KONFIGURATION ---
set "BASEDIR=%~dp0"
set "LOG_DIR=%BASEDIR%logs"
set "SERVER_EXE=%BASEDIR%apps\ez-booth-server\ez-booth-server.exe"
set "UI_EXE=%BASEDIR%apps\ez-booth-vaadin-ui\ez-booth-vaadin-ui.exe"

:: Ports definieren
set "HTTP_PORT=8091"
set "GRPC_PORT=9090"
set "UI_PORT=8090"

echo 🧹 1. EMERGENCY CLEANUP...

:: Wir prüfen jeden Port einzeln ohne verschachtelte FOR-Schleifen
for /f "tokens=5" %%A in ('netstat -ano ^| findstr ":%HTTP_PORT%"') do taskkill /F /PID %%A /T >nul 2>&1
for /f "tokens=5" %%A in ('netstat -ano ^| findstr ":%GRPC_PORT%"') do taskkill /F /PID %%A /T >nul 2>&1
for /f "tokens=5" %%A in ('netstat -ano ^| findstr ":%UI_PORT%"') do taskkill /F /PID %%A /T >nul 2>&1

:: Namentliches Aufräumen ohne Klammer-Block
taskkill /F /IM ez-booth-server.exe /T >nul 2>&1
taskkill /F /IM ez-booth-vaadin-ui.exe /T >nul 2>&1

timeout /t 2 /nobreak >nul

if not exist "%LOG_DIR%" mkdir "%LOG_DIR%"

echo 🚀 2. STARTING SUITE...

:: Timestamp für eindeutige Log-Files (verhindert "File in use" Fehler)
set "TS=%time:~0,2%%time:~3,2%%time:~6,2%"
set "TS=%TS: =0%"
set "CURRENT_LOG=%LOG_DIR%\server_%TS%.log"

echo   ^> Server Log: logs\server_%TS%.log

:: PID-Hilfsdatei erstellen
set "PID_FILE=%TEMP%\ez_pid_%RANDOM%.txt"

echo   ^> Launching Server Process...
:: Start mit FORK-Mechanismus für Java 25 Stabilität
powershell -NoProfile -Command "$p = Start-Process -FilePath '%SERVER_EXE%' -ArgumentList '-Djdk.lang.Process.launchMechanism=FORK' -RedirectStandardOutput '%CURRENT_LOG%' -PassThru; $p.Id | Out-File -FilePath '%PID_FILE%' -Encoding ascii"

:: Warten auf die PID-Datei (Logik korrigiert: Retry-Reset ausserhalb der Schleife)
set /a RETRY_COUNT=0
:WAIT_PID_LOOP
if not exist "%PID_FILE%" (
    timeout /t 1 /nobreak >nul
    set /a RETRY_COUNT+=1
    if !RETRY_COUNT! gtr 10 goto :PID_FAIL
    goto :WAIT_PID_LOOP
)

set /p SERVER_PID=<%PID_FILE%
del "%PID_FILE%" >nul 2>&1

echo   ^> Server started (PID: %SERVER_PID%). Waiting for Port %GRPC_PORT%...

:: --- PORT CHECK LOGIK (gRPC) ---
set /a COUNTER=0
set "READY=0"

:PORT_LOOP
set /a COUNTER+=1

:: Suche nach dem Port im LISTENING Status
netstat -ano | findstr /C:":%GRPC_PORT% " >nul
if !errorlevel! equ 0 (
    set "READY=1"
    goto :SERVER_READY
)

echo     ... waiting for gRPC on %GRPC_PORT% (Attempt !COUNTER!/45)
:: Stabiler 1-Sekunden-Timer via PING
ping -n 2 127.0.0.1 >nul

if !COUNTER! gtr 45 goto :SERVER_TIMEOUT
goto :PORT_LOOP

:SERVER_TIMEOUT
echo ❌ ERROR: Server did not start on port %GRPC_PORT% in time.
start notepad "%CURRENT_LOG%"
taskkill /PID %SERVER_PID% /F /T >nul 2>&1
pause
exit /b 1

:SERVER_READY
echo ✅ Server ist bereit!
echo.
echo --------------------------------------------------
echo 🌐 URL: http://localhost:%HTTP_PORT%
echo.
echo 👉 Druecke eine beliebige Taste in diesem Fenster,
echo    um die Suite VOLLSTAENDIG zu BEENDEN.
echo --------------------------------------------------

:: UI starten (startet den Browser und "verabschiedet" sich)
start "" "%UI_EXE%" -Djdk.lang.Process.launchMechanism=FORK

:: Hier bleibt das Skript stehen, bis der User eine Taste drückt
pause >nul

echo.
echo 👋 Beende alle Prozesse...
taskkill /PID %SERVER_PID% /F /T >nul 2>&1

:: 2. Die UI-Exe explizit beenden (falls sie noch im Hintergrund haengt)
taskkill /F /IM ez-booth-vaadin-ui.exe /T >nul 2>&1

echo ✅ Alles sauber beendet. Bis zum naechsten Mal!
timeout /t 2 >nul
exit /b 0

:PID_FAIL
echo ❌ FATAL: Could not capture Server PID. Check logs.
pause
exit /b 1