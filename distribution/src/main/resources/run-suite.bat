@echo off
setlocal EnableExtensions EnableDelayedExpansion

set "BASEDIR=%~dp0"
set "LOG_DIR=%BASEDIR%logs"
set "SERVER_EXE=%BASEDIR%apps\ez-booth-server\ez-booth-server.exe"
set "HTTP_PORT=8091"
set "GRPC_PORT=9090"
set "UI_EXE=%BASEDIR%apps\ez-booth-vaadin-ui\ez-booth-vaadin-ui.exe"
set "UI_PORT=8090"

echo 🧹 1. EMERGENCY CLEANUP...

:: 1. Alle Prozesse beenden, die auf den Ports lauschen
:: KORREKTUR: Variablen-Namen korrigiert (!GRPC_PORT! statt !SERVER_PORT!)
for %%P in (!HTTP_PORT! !GRPC_PORT! !UI_PORT!) do (
    for /f "tokens=5" %%A in ('netstat -ano ^| findstr /C:":%%P " ^| findstr "LISTENING"') do (
        echo   ^> Found zombie process on port %%P (PID: %%A). Killing it...
        taskkill /F /PID %%A /T >nul 2>&1
    )
)

:: 2. Sicherheitshalber namentlich aufräumen
taskkill /F /IM ez-booth-server.exe /T >nul 2>&1
taskkill /F /IM ez-booth-vaadin-ui.exe /T >nul 2>&1

timeout /t 2 /nobreak >nul

if not exist "%LOG_DIR%" mkdir "%LOG_DIR%"

echo 🚀 2. STARTING SUITE...

:: Timestamp für Log-Datei
set "TS=%time:~0,2%%time:~3,2%%time:~6,2%"
set "TS=%TS: =0%"
set "CURRENT_LOG=%LOG_DIR%\server_%TS%.log"

echo   ^> Server Log: logs\server_%TS%.log

set "PID_FILE=%TEMP%\ez_pid_%RANDOM%.txt"

echo   ^> Launching Server Process...
:: KORREKTUR: -ArgumentList hinzugefügt für stabilen Prozess-Start
powershell -NoProfile -Command "$p = Start-Process -FilePath '%SERVER_EXE%' -ArgumentList '-Djdk.lang.Process.launchMechanism=FORK' -RedirectStandardOutput '%CURRENT_LOG%' -PassThru; $p.Id | Out-File -FilePath '%PID_FILE%' -Encoding ascii"

:WAIT_PID
set "RETRY=0"
if not exist "%PID_FILE%" (
    timeout /t 1 /nobreak >nul
    set /a RETRY+=1
    if !RETRY! gtr 10 goto :PID_FAIL
    goto :WAIT_PID
)

set /p SERVER_PID=<%PID_FILE%
del "%PID_FILE%" >nul 2>&1

echo   ^> Server PID: %SERVER_PID% - Checking Port !GRPC_PORT!...

:: --- PORT CHECK LOGIK ---
set /a COUNTER=0
set "READY=0"

:PORT_LOOP
set /a COUNTER+=1

:: KORREKTUR: Nutze % statt ! für die Variable hier, da sie sich in der Schleife nicht ändert
netstat -ano | findstr /C:":%GRPC_PORT% " | findstr "LISTENING" >nul
if !errorlevel! equ 0 (
    set "READY=1"
    goto :SERVER_READY
)

echo     ... waiting for server start (Attempt !COUNTER!/45)
ping -n 2 127.0.0.1 >nul

if !COUNTER! gtr 45 goto :SERVER_TIMEOUT
goto :PORT_LOOP

:SERVER_TIMEOUT
echo ❌ ERROR: Server did not start on port !GRPC_PORT! in time.
start notepad "%CURRENT_LOG%"
taskkill /PID %SERVER_PID% /F /T >nul 2>&1
pause
exit /b 1

:SERVER_READY
echo ✅ Server is UP and listening!

echo   ^> Starting UI...
echo --------------------------------------------------
echo 🌐 URL: http://localhost:!UI_PORT!
echo --------------------------------------------------

:: UI starten
"%UI_EXE%" -Djdk.lang.Process.launchMechanism=FORK

echo 👋 UI closed. Cleaning up all processes...
taskkill /PID %SERVER_PID% /F /T >nul 2>&1
exit /b 0

:PID_FAIL
echo ❌ FATAL: Could not capture Server PID.
pause
exit /b 1