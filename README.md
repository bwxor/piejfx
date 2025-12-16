# piejfx - A cross-platform, Java-FX version of pie

## Contribute

### Build steps

#### Step 1: Create a jar file
You can do this through the `mvn package` command (or directly from IntelliJ's Maven interface).

#### Step 2: Move the jar (together with its dependencies) in a separate folder
- From `target/`, take your generated jar
- Also type `mvn dependency:copy-dependencies -DoutputDirectory=<DIRECTORY_NAME>` in order to move the necessary libraries into your dir

#### Step 3: Use `jpackage` to create a binary file

#### For Windows
```powershell
jpackage --type exe --input <CREATED_FOLDER> --name Pie --main-jar <GENERATED_JAR_FILE> --main-class com.bwxor.piejfx.Launcher --dest output --win-dir-chooser --win-menu --win-shortcut --icon <ICON_FILE>
```
Also include the `--win-console` flag and run your exectuable from a terminal, in order to see error messages on application launch. Make sure you're using the JPackage integrated with the Java 25 SDK or later. In order to see the version, type `jpackage --version`.

The icon file is located under `piejfx/src/main/resources/com/bwxor/piejfx/img/icons/icon.ico`.

#### For Linux
```bash
jpackage --type deb --input <CREATED_FOLDER> --name Pie --main-jar <GENERATED_JAR_FILE> --main-class com.bwxor.piejfx.Launcher --dest output --linux-shortcut --linux-menu-group "Utility" --icon <ICON_FILE>
```

