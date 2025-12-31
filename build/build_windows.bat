@echo off
REM Pie for Windows

echo Removing residual files
if exist "deps\" (
	del /f /s /q deps
	rmdir deps
)

if exist "output\" (
	del /f /s /q output
	rmdir output
)

echo Building the plugin jar...
cd ../piejfx-plugin-core
call mvn clean install

if %errorlevel% neq 0 (
    echo mvn clean install failed with error code %errorlevel%.
    exit /b %errorlevel%
)

echo Building the project...
cd ../piejfx
call mvn clean install

if %errorlevel% neq 0 (
    echo mvn clean install failed with error code %errorlevel%.
    exit /b %errorlevel%
)

cd ../build
echo Copying created jar into deps folder...

mkdir deps
move /Y ..\piejfx\target\pie.jar deps\pie.jar

echo Copying jar dependencies files into deps folder...
cd ../piejfx
call mvn dependency:copy-dependencies -DoutputDirectory=../build/deps
if %errorlevel% neq 0 (
    echo mvn copy-dependencies failed with error code %errorlevel%.
    exit /b %errorlevel%
)

echo Running jpackage command...
cd ../build
call jpackage --type exe --input deps --name Pie --main-jar pie.jar --main-class com.bwxor.piejfx.Launcher --dest output --win-dir-chooser --win-menu --win-shortcut --icon ../piejfx/src/main/resources/com/bwxor/piejfx/img/icons/icon.ico
if %errorlevel% neq 0 (
    echo jpackage failed with error code %errorlevel%.
    exit /b %errorlevel%
)

echo Build success.

pause