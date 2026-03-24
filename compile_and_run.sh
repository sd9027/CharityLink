#!/bin/bash

echo ""
echo "  ============================================="
echo "   CharityLink - Build and Run"
echo "  ============================================="
echo ""

# Check java
if ! command -v java &> /dev/null; then
    echo "  [ERROR] Java not found. Install JDK 17+ from https://adoptium.net"
    exit 1
fi

# Check javac
if ! command -v javac &> /dev/null; then
    echo "  [ERROR] javac not found. Install JDK (not just JRE)."
    echo "  Mac:   brew install --cask temurin"
    echo "  Linux: sudo apt install default-jdk"
    exit 1
fi

# Create dirs
mkdir -p out data

echo "  [1/3] Compiling source files..."

# Find all .java files and compile
find src -name "*.java" > sources.txt
javac -d out @sources.txt

if [ $? -ne 0 ]; then
    echo ""
    echo "  [ERROR] Compilation failed."
    rm -f sources.txt
    exit 1
fi

rm -f sources.txt
echo "  [2/3] Compilation successful!"
echo "  [3/3] Launching CharityLink..."
echo ""

java -cp out charitylink.Main
