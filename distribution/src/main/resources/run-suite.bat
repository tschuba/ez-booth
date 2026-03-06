@echo off
setlocal EnableExtensions EnableDelayedExpansion

:: --- SETTINGS ---
set "BASEDIR=%~dp0"
set "LOG_DIR=%BASEDIR%logs"
set "SERVER_EXE=%BASEDIR%apps\ez-booth-server\ez-booth-server.exe"
set "UI_EXE=%BASEDIR%apps\ez-booth-vaadin-ui\ez-booth-vaadin-ui.exe"

set "HTTP_PORT=8091"
set "GRPC_PORT=9090"
set "UI_PORT=8090"

echo 🧹 Cleaning up previous sessions...

:: Terminate processes by port (Language-neutral)
for /f "tokens=5" %%A in ('netstat -ano ^| findstr /C:":%GRPC_PORT% "') do taskkill /F /PID %%A /T >nul 2>&1
for /f "tokens=5" %%A in ('netstat -ano ^| findstr /C:":%HTTP_PORT% "') do taskkill /F /PID %%A /T >nul 2>&1
for /f "tokens=5" %%A in ('netstat -ano ^| findstr /C:":%UI_PORT% "') do taskkill /F /PID %%A /T >nul 2>&1

:: Terminate processes by name
taskkill /F /IM ez-booth-server.exe /T >nul 2>&1
taskkill /F /IM ez-booth-vaadin-ui.exe /T >nul 2>&1

timeout /t 2 /nobreak >nul
if not exist "%LOG_DIR%" mkdir "%LOG_DIR%"

echo 🚀 Starting Suite...

:: Generate timestamp for unique logging
set "cur_time=%time: =0%"
set "TS=%cur_time:~0,2%%cur_time:~3,2%%cur_time:~6,2%"
set "CURRENT_LOG=%LOG_DIR%\server_%TS%.log"
set "PID_FILE=%TEMP%\ez_pid_%RANDOM%.txt"

:: Launch server via PowerShell to capture PID
powershell -NoProfile -Command "$p = Start-Process -FilePath '%SERVER_EXE%' -ArgumentList '-Djdk.lang.Process.launchMechanism=FORK' -RedirectStandardOutput '%CURRENT_LOG%' -PassThru; $p.Id | Out-File -FilePath '%PID_FILE%' -Encoding ascii"

:: PID detection loop
set /a RETRY_COUNT=0
:WAIT_PID
if not exist "%PID_FILE%" (
    timeout /t 1 /nobreak >nul
    set /a RETRY_COUNT+=1
    if !RETRY_COUNT! gtr 10 goto :PID_FAIL
    goto :WAIT_PID
)

set /p SERVER_PID=<%PID_FILE%
del "%PID_FILE%" >nul 2>&1

echo   ^> Server active (PID: %SERVER_PID%). Initializing...

:: Port availability check
set /a COUNTER=0
:PORT_LOOP
set /a COUNTER+=1
netstat -ano | findstr /C:":%GRPC_PORT% " >nul
if !errorlevel! equ 0 goto :SERVER_READY

if !COUNTER! gtr 45 goto :SERVER_TIMEOUT
echo   ^> Waiting for port %GRPC_PORT%... (!COUNTER!/45)
ping -n 2 127.0.0.1 >nul
goto :PORT_LOOP

:SERVER_TIMEOUT
echo ❌ ERROR: Server timed out.
start notepad "%CURRENT_LOG%"
taskkill /PID %SERVER_PID% /F /T >nul 2>&1
pause
exit /b 1

:SERVER_READY
echo ✅ Suite is running!
echo --------------------------------------------------
echo 🌐 URL: http://localhost:%HTTP_PORT%
echo 👉 Press any key to SHUT DOWN.
echo --------------------------------------------------

:: Launch UI detached
start "" "%UI_EXE%" -Djdk.lang.Process.launchMechanism=FORK

pause >nul

echo 🧹 Shutting down...

:: Comprehensive process termination
taskkill /F /PID %SERVER_PID% /T >nul 2>&1
taskkill /F /IM ez-booth-vaadin-ui.exe /T >nul 2>&1

:: Final port cleanup
for /f "tokens=5" %%A in ('netstat -ano ^| findstr /C:":%GRPC_PORT% "') do taskkill /F /PID %%A /T >nul 2>&1
for /f "tokens=5" %%A in ('netstat -ano ^| findstr /C:":%HTTP_PORT% "') do taskkill /F /PID %%A /T >nul 2>&1
for /f "tokens=5" %%A in ('netstat -ano ^| findstr /C:":%UI_PORT% "') do taskkill /F /PID %%A /T >nul 2>&1

echo ✅ Done.
timeout /t 2 >nul
exit /b 0

:PID_FAIL
echo ❌ FATAL: PID capture failed.
pause
exit /b 1