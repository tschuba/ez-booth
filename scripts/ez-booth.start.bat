@echo off
setlocal

rem Default values
set "BACKEND_PORT=8080"
set "JAR_VERSION=1.0.0"
set "TIMEOUT=60"

rem Parse named options
:parseArgs
if "%~1"=="" goto argsDone
if "%~1"=="--port" (
    set "BACKEND_PORT=%~2"
    shift
    shift
    goto parseArgs
)
if "%~1"=="--version" (
    set "JAR_VERSION=%~2"
    shift
    shift
    goto parseArgs
)
echo Unknown option %~1
echo Usage: %~nx0 [--port <port>] [--version <jar-version>]
goto endScript

:argsDone

echo Starting backend ez-booth-server (version %JAR_VERSION%) on port %BACKEND_PORT%...
start "ez-booth-server" java -jar ez-booth-server-%JAR_VERSION%.jar

if errorlevel 1 (
    echo ERROR: Backend process failed to start. Check ez-booth-server-%JAR_VERSION%.jar and Java installation.
    goto endScript
)

rem Wait until backend is ready (timeout: 60 seconds)
echo Checking if backend is ready (timeout: %TIMEOUT% seconds)...
set /a waited=0
:waitForBackend
curl -sf http://localhost:%BACKEND_PORT%/actuator/health >nul 2>&1
if errorlevel 1 (
    rem Check if backend process is still running
    tasklist | findstr /I "java.exe" >nul
    if errorlevel 1 (
        echo ERROR: Backend process terminated unexpectedly.
        goto killApps
    )
    set /a waited+=2
    if %waited% GEQ %TIMEOUT% (
        echo ERROR: Backend did not become ready within %TIMEOUT% seconds.
        goto killApps
    )
    echo Waiting for backend ez-booth-server on port %BACKEND_PORT%...
    timeout /t 2 >nul
    goto waitForBackend
)

echo Backend is ready! Starting frontend (version %JAR_VERSION%)...
start "frontend-app" java -jar ez-booth-vaadin-ui-%JAR_VERSION%.jar

rem Wait for user to close batch (simulate "wait")
echo Press Ctrl+C to stop both applications.
:waitLoop
timeout /t 5 >nul
goto waitLoop

rem Cleanup section: Kill both Java apps
:killApps
echo Stopping applications...
taskkill /FI "WINDOWTITLE eq ez-booth-server*" /T /F >nul
taskkill /FI "WINDOWTITLE eq frontend-app*" /T /F >nul
echo Applications stopped.

:endScript
endlocal