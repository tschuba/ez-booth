@echo off
setlocal EnableExtensions EnableDelayedExpansion

:: --- CONFIGURATION ---
set "BASEDIR=%~dp0"
set "LOG_DIR=%BASEDIR%logs"
set "SERVER_EXE=%BASEDIR%apps\ez-booth-server\ez-booth-server.exe"
set "UI_EXE=%BASEDIR%apps\ez-booth-vaadin-ui\ez-booth-vaadin-ui.exe"

:: Port Definitions
set "HTTP_PORT=8091"
set "GRPC_PORT=9090"
set "UI_PORT=8090"

echo 🧹 1. EMERGENCY CLEANUP...

:: Language-independent cleanup: Kill any process listening on our specific ports
for /f "tokens=5" %%A in ('netstat -ano ^| findstr /C:":%GRPC_PORT% "') do taskkill /F /PID %%A /T >nul 2>&1
for /f "tokens=5" %%A in ('netstat -ano ^| findstr /C:":%HTTP_PORT% "') do taskkill /F /PID %%A /T >nul 2>&1
for /f "tokens=5" %%A in ('netstat -ano ^| findstr /C:":%UI_PORT% "') do taskkill /F /PID %%A /T >nul 2>&1

:: Additional cleanup by process name
taskkill /F /IM ez-booth-server.exe /T >nul 2>&1
taskkill /F /IM ez-booth-vaadin-ui.exe /T >nul 2>&1

:: Wait for Windows to release file locks
timeout /t 2 /nobreak >nul

if not exist "%LOG_DIR%" mkdir "%LOG_DIR%"

echo 🚀 2. STARTING SUITE...

:: Create unique timestamp for log file to avoid "File in use" errors
set "cur_time=%time: =0%"
set "TS=%cur_time:~0,2%%cur_time:~3,2%%cur_time:~6,2%"
set "CURRENT_LOG=%LOG_DIR%\server_%TS%.log"

echo   ^> Server Log: logs\server_%TS%.log

:: Temporary file to capture the Server PID
set "PID_FILE=%TEMP%\ez_pid_%RANDOM%.txt"

echo   ^> Launching Server Process...
:: Using FORK mechanism for better process management
powershell -NoProfile -Command "$p = Start-Process -FilePath '%SERVER_EXE%' -ArgumentList '-Djdk.lang.Process.launchMechanism=FORK' -RedirectStandardOutput '%CURRENT_LOG%' -PassThru; $p.Id | Out-File -FilePath '%PID_FILE%' -Encoding ascii"

:: Wait for the PID file to be created
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

echo   ^> Server started (Parent PID: %SERVER_PID%). Checking Port %GRPC_PORT%...

:: --- PORT CHECK LOGIC (Language-neutral) ---
set /a COUNTER=0
set "READY=0"

:PORT_LOOP
set /a COUNTER+=1

:: Check if port is active (any status). Since we cleaned up before, a hit belongs to our app.
netstat -ano | findstr /C:":%GRPC_PORT% " >nul
if !errorlevel! equ 0 (
    set "READY=1"
    goto :SERVER_READY
)

echo     ... waiting for server on port %GRPC_PORT% (Attempt !COUNTER!/45)
:: Stable 1-second delay using PING
ping -n 5 127.0.0.1 >nul

if !COUNTER! gtr 45 goto :SERVER_TIMEOUT
goto :PORT_LOOP

:SERVER_TIMEOUT
echo ❌ ERROR: Server did not start on port %GRPC_PORT% in time.
start notepad "%CURRENT_LOG%"
taskkill /PID %SERVER_PID% /F /T >nul 2>&1
pause
exit /b 1

:SERVER_READY
echo ✅ Server is UP and listening!

echo.
echo --------------------------------------------------
echo 🌐 URL: http://localhost:%HTTP_PORT%
echo.
echo 👉 PRESS ANY KEY IN THIS WINDOW TO STOP THE SUITE.
echo --------------------------------------------------

:: Launch UI as a detached process
start "" "%UI_EXE%" -Djdk.lang.Process.launchMechanism=FORK

:: Keep script alive and responsive to user input
pause >nul

echo.
echo 🧹 Shutting down all processes and clearing ports...

:: 1. Kill the server process tree (Parent + Java Subprocess)
taskkill /F /PID %SERVER_PID% /T >nul 2>&1

:: 2. Explicitly kill the UI launcher (in case it hangs in background)
taskkill /F /IM ez-booth-vaadin-ui.exe /T >nul 2>&1

:: 3. Final safety sweep of all ports
for /f "tokens=5" %%A in ('netstat -ano ^| findstr /C:":%GRPC_PORT% "') do taskkill /F /PID %%A /T >nul 2>&1
for /f "tokens=5" %%A in ('netstat -ano ^| findstr /C:":%HTTP_PORT% "') do taskkill /F /PID %%A /T >nul 2>&1
for /f "tokens=5" %%A in ('netstat -ano ^| findstr /C:":%UI_PORT% "') do taskkill /F /PID %%A /T >nul 2>&1

echo ✅ Shutdown complete.
timeout /t 2 >nul
exit /b 0

:PID_FAIL
echo ❌ FATAL: Could not capture Server PID. Check logs.
pause
exit /b 1