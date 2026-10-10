#!/bin/bash

# Pie for Linux - RPM build

# jpackage needs rpmbuild to produce an .rpm
if ! command -v rpmbuild >/dev/null 2>&1; then
    echo "rpmbuild not found."
    echo "Install it with: sudo dnf install rpm-build   (Fedora/RHEL)"
    echo "             or: sudo apt install rpm         (Debian/Ubuntu)"
    exit 1
fi

echo "Removing residual files..."
rm -rf deps/
rm -rf output/

echo "Building the plugin jar..."
cd ../piejfx-plugin-core || exit
mvn clean install

if [ $? -ne 0 ]; then
    echo "mvn clean install failed."
    exit 1
fi

echo "Building the project..."
cd ../piejfx || exit
mvn clean install

if [ $? -ne 0 ]; then
    echo "mvn clean install failed."
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

jpackage --type rpm \
  --input deps \
  --name Pie \
  --main-jar pie.jar \
  --main-class com.bwxor.piejfx.Launcher \
  --dest output \
  --icon ../piejfx/src/main/resources/com/bwxor/piejfx/img/icons/icon.png \
  --linux-shortcut

if [ $? -ne 0 ]; then
    echo "jpackage failed."
    exit 1
fi

echo "Build success. RPM is in the output folder."
read -p "Press enter to continue..."