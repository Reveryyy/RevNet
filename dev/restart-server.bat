@echo off
setlocal EnableExtensions

REM ============================================================
REM ====================== DEV ONLY =============================
REM ============================================================
REM
REM RevNet - sviluppo locale
REM
REM %1 = JAR compilato da Maven
REM
REM Workflow:
REM
REM   1. Controlla se Paper e' attivo
REM   2. Se attivo -> STOP tramite RCON
REM   3. Aspetta la chiusura
REM   4. Copia il nuovo JAR
REM   5. Avvia Paper
REM
REM ============================================================


REM ============================================================
REM CONFIGURAZIONE
REM ============================================================

set "SERVER_DIR=C:\Users\phred\.localhost\1.21.4"
set "SERVER_JAR=server.jar"
set "PLUGIN_DIR=%SERVER_DIR%\plugins"
set "PLUGIN_NAME=revnet-1.0.0.jar"

set "JAVA_CMD=java"
set "JAVA_ARGS=-Xms2G -Xmx4G"

set "RCON_PORT=25575"
set "RCON_PASSWORD=@Valerio09@"


REM ============================================================
REM CONTROLLO ARGOMENTO
REM ============================================================

if "%~1"=="" (
    echo.
    echo [DEV] ERRORE: nessun JAR ricevuto da Maven.
    echo.
    exit /b 1
)

set "BUILD_JAR=%~1"


REM ============================================================
REM CONTROLLO FILE
REM ============================================================

if not exist "%BUILD_JAR%" (
    echo.
    echo [DEV] ERRORE: JAR non trovato:
    echo [DEV] %BUILD_JAR%
    echo.
    exit /b 1
)

if not exist "%SERVER_DIR%\%SERVER_JAR%" (
    echo.
    echo [DEV] ERRORE: server.jar non trovato:
    echo [DEV] %SERVER_DIR%\%SERVER_JAR%
    echo.
    exit /b 1
)


REM ============================================================
REM HEADER
REM ============================================================

echo.
echo ============================================================
echo                 REVNET DEVELOPMENT
echo ============================================================
echo.
echo [DEV] JAR:    %BUILD_JAR%
echo [DEV] SERVER: %SERVER_DIR%
echo.


REM ============================================================
REM CONTROLLO PAPER
REM ============================================================

echo [DEV] Controllo se Paper e' avviato...

powershell.exe -NoProfile -Command "$p = Get-CimInstance Win32_Process | Where-Object { $_.Name -eq 'java.exe' -and $_.CommandLine -like '*-jar*server.jar*' }; if ($p) { exit 0 } else { exit 1 }"

if errorlevel 1 (
    echo [DEV] Paper non e' avviato.
    goto COPY_PLUGIN
)

echo [DEV] Paper e' avviato.
echo [DEV] Invio STOP...


REM ============================================================
REM STOP RCON
REM ============================================================

powershell.exe -NoProfile -ExecutionPolicy Bypass -Command "$ErrorActionPreference='Stop'; $tcp=New-Object System.Net.Sockets.TcpClient('127.0.0.1',%RCON_PORT%); $stream=$tcp.GetStream(); function Send-Packet([int]$id,[int]$type,[string]$body) { $b=[Text.Encoding]::UTF8.GetBytes($body); $p=New-Object byte[] ($b.Length+10); [BitConverter]::GetBytes($id).CopyTo($p,0); [BitConverter]::GetBytes($type).CopyTo($p,4); $b.CopyTo($p,8); $p[$b.Length+8]=0; $p[$b.Length+9]=0; $l=[BitConverter]::GetBytes($p.Length); $packet=New-Object byte[] ($p.Length+4); $l.CopyTo($packet,0); $p.CopyTo($packet,4); $stream.Write($packet,0,$packet.Length) }; Send-Packet 1 3 '%RCON_PASSWORD%'; Send-Packet 2 2 'stop'; $tcp.Close()"

if errorlevel 1 (
    echo.
    echo [DEV] ERRORE: impossibile inviare STOP tramite RCON.
    echo [DEV] Controlla server.properties.
    echo.
    exit /b 1
)

echo [DEV] STOP inviato.
echo [DEV] Attendo la chiusura di Paper...


REM ============================================================
REM ATTESA CHIUSURA
REM ============================================================

:WAIT_FOR_PAPER

timeout /t 1 /nobreak >nul

powershell.exe -NoProfile -Command "$p = Get-CimInstance Win32_Process | Where-Object { $_.Name -eq 'java.exe' -and $_.CommandLine -like '*-jar*server.jar*' }; if ($p) { exit 0 } else { exit 1 }"

if not errorlevel 1 goto WAIT_FOR_PAPER

echo [DEV] Paper chiuso.


REM ============================================================
REM COPIA JAR
REM ============================================================

:COPY_PLUGIN

echo.
echo [DEV] Copio il nuovo JAR...

if not exist "%PLUGIN_DIR%" (
    echo [DEV] Creo la cartella plugins...
    mkdir "%PLUGIN_DIR%"
)

copy /Y "%BUILD_JAR%" "%PLUGIN_DIR%\%PLUGIN_NAME%" >nul

if errorlevel 1 (
    echo.
    echo [DEV] ERRORE durante la copia del JAR.
    echo.
    exit /b 1
)

echo [DEV] JAR copiato.


REM ============================================================
REM AVVIO PAPER
REM ============================================================

echo.
echo [DEV] Avvio Paper...
echo.

cd /d "%SERVER_DIR%"

start "Paper Dev Server" cmd /c "%JAVA_CMD% %JAVA_ARGS% -jar %SERVER_JAR% nogui"

echo.
echo ============================================================
echo                    PAPER AVVIATO
echo ============================================================
echo.

endlocal
exit /b 0