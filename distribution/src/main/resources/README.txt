================================================================================
                          EZ-BOOTH SUITE - README
================================================================================

QUICK START
-----------

Windows Users:
    Double-click "run-suite.bat" or run it from command prompt:
    > run-suite.bat

macOS/Linux Users:
    Open Terminal in this directory and execute:
    $ ./run-suite.sh

    Note: If you get a permission error, make the script executable first:
    $ chmod +x run-suite.sh


WHAT HAPPENS WHEN YOU START
----------------------------

1. The launcher automatically stops any old processes running on ports 8091
   and 8090 to prevent conflicts.

2. The ez-booth-server starts in the background on port 8091.
   - Server logs are written to: logs/server.log

3. After a brief initialization pause, the launcher verifies that the server
   started successfully.

4. The ez-booth-vaadin-ui starts in the foreground on port 8090.
   - UI logs are written to: logs/ui.log
   - Your default browser should open automatically to:
     http://localhost:8090

5. When you close the UI application, the server is automatically shut down.


TROUBLESHOOTING
---------------

Server won't start:
    - Check logs/server.log for error details
    - On Windows, the log folder opens automatically on startup failure
    - Ensure ports 8090 and 8091 are not blocked by firewall
    - Verify Java runtime is properly bundled with the applications

UI won't load:
    - Check logs/ui.log for error details
    - Manually navigate to http://localhost:8090 in your browser
    - Ensure the server started successfully first

Port conflicts:
    - The launcher automatically kills old processes on startup
    - If manual cleanup is needed:
      Windows: Use Task Manager to end ez-booth processes
      macOS/Linux: Use Activity Monitor or run: killall -9 ez-booth-server


SYSTEM REQUIREMENTS
-------------------

Windows:
    - Windows 10 or later (64-bit)
    - PowerShell (pre-installed on modern Windows)

macOS:
    - macOS 10.14 or later
    - Terminal access

Linux:
    - Most modern distributions (tested on Ubuntu 20.04+)
    - bash shell
    - lsof utility (usually pre-installed)


LOGS & DIAGNOSTICS
------------------

All application logs are stored in the "logs" subfolder:
    - server.log: Backend application output
    - ui.log: Frontend application output

Each new session appends a timestamp header to server.log for easy tracking.


STOPPING THE APPLICATION
-------------------------

Normal shutdown:
    Simply close the UI window/application. The server will automatically
    shut down within a few seconds.

Force stop (if needed):
    Windows:
        > taskkill /F /IM ez-booth-server.exe
        > taskkill /F /IM ez-booth-vaadin-ui.exe

    macOS/Linux:
        $ killall -9 ez-booth-server
        $ killall -9 ez-booth-vaadin-ui


ADVANCED USAGE
--------------

The launchers use fixed ports by default:
    - Server: 8091
    - UI: 8090

If you need to change these ports or run multiple instances, you'll need to
modify the launcher scripts directly or use the JAR-based deployment method
documented in the main project documentation.


SUPPORT
-------

For issues, bugs, or feature requests, please contact:
    Thomas Schulte-Bahrenberg
    https://github.com/tschuba/ez-booth

================================================================================
Copyright (c) 2026 Thomas Schulte-Bahrenberg. All rights reserved.
================================================================================

