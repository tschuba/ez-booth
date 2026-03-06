@echo off
setlocal EnableExtensions EnableDelayedExpansion

set "BASEDIR=%~dp0"
set "UI_PORT=8090"
set "SERVER_PORT=8091"
set "LOG_DIR=%BASEDIR%logs"
set "SERVER_LOG=%LOG_DIR%\server.log"
set "UI_LOG=%LOG_DIR%\ui.log"

echo 🔍 Cleaning up old sessions...
for %%P in (%SERVER_PORT% %UI_PORT%) do (
    for /f "tokens=5" %%A in ('netstat -ano ^| findstr /R /C:":%%P .*LISTENING"') do (
        echo   ^> Closing old process on port %%P (PID: %%A^)...
        taskkill /PID %%A /F >nul 2>&1
    )
)

echo 🚀 Starting ez-booth Suite...

if not exist "%LOG_DIR%" mkdir "%LOG_DIR%"
echo --- New Session %date% %time% --- >> "%SERVER_LOG%"

echo   ^> Starting Server...
:: Wir entfernen RedirectStandardError, da PowerShell nicht zweimal dieselbe Datei öffnen kann
for /f "delims=" %%I in ('powershell -NoProfile -Command "$p=Start-Process -FilePath '%BASEDIR%apps\ez-booth-server\ez-booth-server.exe' -RedirectStandardOutput '%SERVER_LOG%' -PassThru; $p.Id"') do set "SERVER_PID=%%I"

:: ... (Timeout und Check wie gehabt) ...

echo   ^> Starting UI...
:: Auch hier nur einen Redirect nutzen
powershell -NoProfile -Command "Start-Process -FilePath '%BASEDIR%apps\ez-booth-vaadin-ui\ez-booth-vaadin-ui.exe' -ArgumentList '-Djdk.lang.Process.launchMechanism=FORK' -RedirectStandardOutput '%UI_LOG%' -Wait"

:: Falls die PID leer ist, gab es einen fatalen Fehler beim Start
if "%SERVER_PID%"=="" (
    echo ❌ Failed to invoke Server process!
    pause
    exit /b 1
)

timeout /t 3 /nobreak >nul

:: KORREKTUR: Robusterer Task-Check ohne den pingeligen /FI Filter
tasklist | findstr /C:" %SERVER_PID% " >nul
if errorlevel 1 (
    echo ❌ Server process %SERVER_PID% is not running! Opening logs...
    start "" "%SERVER_LOG%"
    exit /b 1
)

echo   ^> Starting UI...
echo --------------------------------------------------
echo 🌐 UI URL: http://localhost:%UI_PORT%
echo --------------------------------------------------

:: UI starten und warten
powershell -NoProfile -Command "Start-Process -FilePath '%BASEDIR%apps\ez-booth-vaadin-ui\ez-booth-vaadin-ui.exe' -ArgumentList '-Djdk.lang.Process.launchMechanism=FORK' -RedirectStandardOutput '%UI_LOG%' -RedirectStandardError '%UI_LOG%' -Wait"

echo 👋 UI closed. Shutting down server...
taskkill /PID %SERVER_PID% /F >nul 2>&1

exit /b 0