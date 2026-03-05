@echo off
echo 🚀 Starting ez-booth Suite...

:: Start server in the background (start /b prevents a blocking window)
:: We use .exe paths for Windows
start /b "" "%~dp0apps\ez-booth-server\ez-booth-server.exe"

:: Short wait (timeout on Windows)
timeout /t 2 /nobreak > nul

:: Start UI (foreground)
echo Starting UI...
call "%~dp0apps\ez-booth-vaadin-ui\ez-booth-vaadin-ui.exe"

:: Note: Automatically stopping the background server when closing
:: the batch file is a bit more complex on Windows, but this is fine for now.
echo UI closed. Please close the server window if it's still open.
pause