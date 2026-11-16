@echo off
REM Compiles and runs the Employee Management System (Windows).
cd /d "%~dp0"

set JAR=
for %%f in (lib\mysql-connector-j-*.jar) do set JAR=%%f
if "%JAR%"=="" (
  echo MySQL JDBC driver not found. Download mysql-connector-j ^(Platform Independent^) from
  echo https://dev.mysql.com/downloads/connector/j/ and place the .jar file in the lib\ folder.
  exit /b 1
)

if not exist out mkdir out
dir /s /b src\*.java > sources.txt
javac -d out @sources.txt
del sources.txt
java -cp "out;%JAR%" com.ems.Main
