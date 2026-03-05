@echo off
setlocal EnableExtensions EnableDelayedExpansion

set "BASEDIR=%~dp0"
set "UI_PORT=8090"
set "SERVER_PORT=8091"
set "LOG_DIR=%BASEDIR%logs"
set "SERVER_LOG=%LOG_DIR%\server.log"
set "UI_LOG=%LOG_DIR%\ui.log"

for %%P in (%SERVER_PORT% %UI_PORT%) do (
    for /f "tokens=5" %%A in ('netstat -ano ^| findstr /R /C:":%%P .*LISTENING"') do (
        if not "%%A"=="0" (
            echo   ^> Closing old process on port %%P (PID: %%A^)...
            taskkill /PID %%A /F >nul 2>&1
        )
    )
)

echo 🚀 Starting ez-booth Suite...

if not exist "%LOG_DIR%" mkdir "%LOG_DIR%"

echo --- New Session %date% %time% --- >> "%SERVER_LOG%"

echo   ^> Starting Server...
for /f %%I in ('powershell -NoProfile -Command "$p=Start-Process -FilePath ''%BASEDIR%apps\ez-booth-server\ez-booth-server.exe'' -RedirectStandardOutput ''%SERVER_LOG%'' -RedirectStandardError ''%SERVER_LOG%'' -PassThru; $p.Id"') do set "SERVER_PID=%%I"

timeout /t 2 /nobreak >nul

tasklist /FI "PID eq %SERVER_PID%" | findstr /R /C:" %SERVER_PID% " >nul
if errorlevel 1 (
    echo ❌ Server failed to start! Opening logs...
    explorer "%LOG_DIR%"
    exit /b 1
)

echo   ^> Starting UI...
echo --------------------------------------------------
echo 🌐 UI URL: http://localhost:%UI_PORT%
echo --------------------------------------------------

powershell -NoProfile -Command "Start-Process -FilePath '%BASEDIR%apps\ez-booth-vaadin-ui\ez-booth-vaadin-ui.exe' -ArgumentList '-Djdk.lang.Process.launchMechanism=FORK' -RedirectStandardOutput '%UI_LOG%' -RedirectStandardError '%UI_LOG%' -Wait"

echo 👋 UI closed. Shutting down server...
taskkill /PID %SERVER_PID% /F >nul 2>&1

exit /b 0
