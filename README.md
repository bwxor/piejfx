# piejfx - A cross-platform, Java-FX version of pie

## Building binaries

The project's build process implies generating a jar file through the Maven build tool and storing it, together with the project's dependency jars, in a separate folder. `jpackage` will be then used on that folder to generate the artifact.

There are two ways of building Pie locally. The first one is the fastest, and requires a single command. The command will automatically create the needed temporary files and the output directory, together with the artifact. You can also build Pie manually, by following the steps from the second subsection.

### Variant 1: Building using the provided scripts
Pie can be built easily by simply running the OS-specific script from the `build/` folder.

### Variant 2: Building manually

#### Step 1: Build the `plugin` project first, then add it to the local Maven repository
This can be done by running the `mvn install` inside the `plugin/` folder.

#### Step 2: Build the `piejfx` project
You can do this through the `mvn package` command (or directly from IntelliJ's Maven interface).

#### Step 3: Move the jar (together with its dependencies) in a separate folder
- From `target/`, take your generated jar
- Also type `mvn dependency:copy-dependencies -DoutputDirectory=<DIRECTORY_NAME>` in order to move the necessary libraries into your dir. The plugin dependency should also be moved, as it is a Maven dependency and exists in the local repository.

#### Step 4: Use `jpackage` to create a binary file

#### For Windows
```powershell
jpackage --type exe --input <CREATED_FOLDER> --name Pie --main-jar <GENERATED_JAR_FILE> --main-class com.bwxor.piejfx.Launcher --dest output --win-dir-chooser --win-menu --win-shortcut --icon <ICON_FILE>
```
Also include the `--win-console` flag and run your exectuable from a terminal, in order to see error messages on application launch. Make sure you're using the JPackage integrated with the Java 25 SDK or later. In order to see the version, type `jpackage --version`.

The Windows icon file is located under `piejfx/src/main/resources/com/bwxor/piejfx/img/icons/icon.ico`.

#### For Linux
```bash
jpackage --type deb --input <CREATED_FOLDER> --name Pie --main-jar <GENERATED_JAR_FILE> --main-class com.bwxor.piejfx.Launcher --dest output --linux-shortcut --linux-menu-group "Utility" --icon <ICON_FILE>
```
The Linux icon file is located under `piejfx/src/main/resources/com/bwxor/piejfx/img/icons/icon.png`. Linux doesn't support `.ico` icons.

## Plugin Development

The plugin development will be done using the `piejfx-sdk`, which is a pre-made Maven project containing JavaFX and a reference to the `plugin/` dependency. 

