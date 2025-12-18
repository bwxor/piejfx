#!/bin/bash

# Pie for Linux

echo "Removing residual files..."
rm -rf deps/
rm -rf output/

echo "Building the project..."
cd ../piejfx || exit
mvn clean package "-Djar.finalName=pie"

if [ $? -ne 0 ]; then
    echo "mvn clean package failed."
    exit 1
fi

cd ../build || exit
echo "Copying created jar into deps folder..."

mkdir -p deps
mv ../piejfx/target/pie.jar deps/pie.jar

echo "Copying jar dependencies files into deps folder..."
cd ../piejfx || exit
mvn dependency:copy-dependencies -DoutputDirectory=../build/deps

if [ $? -ne 0 ]; then
    echo "mvn copy-dependencies failed."
    exit 1
fi

echo "Running jpackage command..."
cd ../build || exit

# Note: Changed --type to app-image for Linux. 
# You can change it to "deb" or "rpm" if you have the tools installed.
# Also replaced .ico with .png as Linux typically uses PNG for icons.
jpackage --type app-image \
  --input deps \
  --name Pie \
  --main-jar pie.jar \
  --main-class com.bwxor.piejfx.Launcher \
  --dest output \
  --icon ../piejfx/src/main/resources/com/bwxor/piejfx/img/icons/icon.ico

if [ $? -ne 0 ]; then
    echo "jpackage failed."
    exit 1
fi

echo "Build success."
read -p "Press enter to continue..."