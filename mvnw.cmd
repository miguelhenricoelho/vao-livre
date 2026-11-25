@echo off
rem Maven Wrapper (Windows). Baixa o Maven fixado em .mvn\wrapper\maven-wrapper.properties
rem na primeira vez (via PowerShell) e repassa todos os argumentos direto para ele.
setlocal
set "RAIZ=%~dp0"
for /f "usebackq tokens=1,* delims==" %%a in ("%RAIZ%.mvn\wrapper\maven-wrapper.properties") do (
  if "%%a"=="distributionUrl" set "URL=%%b"
)
for %%f in ("%URL%") do set "ZIP=%%~nxf"
set "PASTA=%ZIP:-bin.zip=%"
set "DESTINO=%USERPROFILE%\.m2\wrapper\dists\%PASTA%"
set "MVN=%DESTINO%\%PASTA%\bin\mvn.cmd"

if not exist "%MVN%" (
  powershell -NoProfile -ExecutionPolicy Bypass -File "%RAIZ%.mvn\wrapper\mvnw.ps1"
  if errorlevel 1 exit /b 1
)

cd /d "%RAIZ%"
call "%MVN%" %*
exit /b %ERRORLEVEL%
