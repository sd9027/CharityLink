@echo off
title CharityLink Build

echo.
echo  =============================================
echo   CharityLink - Build and Run
echo  =============================================
echo.

:: Check Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo  [ERROR] Java is not installed or not in PATH.
    echo  Please install JDK 17+ from https://adoptium.net
    pause
    exit /b 1
)

javac -version >nul 2>&1
if %errorlevel% neq 0 (
    echo  [ERROR] javac not found. Please install a full JDK (not just JRE).
    echo  Download from https://adoptium.net
    pause
    exit /b 1
)

:: Create output and data directories
if not exist "out"  mkdir out
if not exist "data" mkdir data

echo  [1/3] Compiling source files...

:: Collect all .java files and compile
dir /s /b src\*.java > sources.txt
javac -d out @sources.txt

if %errorlevel% neq 0 (
    echo.
    echo  [ERROR] Compilation failed. Check errors above.
    del sources.txt
    pause
    exit /b 1
)

del sources.txt
echo  [2/3] Compilation successful!
echo  [3/3] Launching CharityLink...
echo.

java -cp out charitylink.Main

pause
