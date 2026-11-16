#!/usr/bin/env bash
# Compiles and runs the Employee Management System (macOS / Linux).
set -e
cd "$(dirname "$0")"

JAR=$(ls lib/mysql-connector-j-*.jar 2>/dev/null | head -n 1)
if [ -z "$JAR" ]; then
  echo "MySQL JDBC driver not found. Download mysql-connector-j (Platform Independent) from"
  echo "https://dev.mysql.com/downloads/connector/j/ and place the .jar file in the lib/ folder."
  exit 1
fi

mkdir -p out
javac -d out $(find src -name "*.java")
java -cp "out:$JAR" com.ems.Main
