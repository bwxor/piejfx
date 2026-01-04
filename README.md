# piejfx - A cross-platform, Java-FX version of pie

## Structure
The repository contains the following projects:
1. `piejfx`: The application itself;
2. `piejfx-plugin-core`: The interface used for establishing communication between piejfx and its plugins;
3. `piejfx-plugin-sdk`: A ready-to-use example plugin *for those who want to contribute with their own ideas*.

## Building binaries

> **Note:** Building piejfx requires building both the base application `piejfx` and the `piejfx-plugin-core` project, as the latter is a (mandatory) dependency of the application and the interface defined inside it is used throughout the code.

In order to build the project, the required jar files will be first generated using Maven, and then `jpackage` will generate the desired artifacts.

There are two ways of building Pie locally. The first one is the fastest, and requires a single command. The command will automatically create the needed temporary files and the output directory, together with the artifact. You can also build Pie manually, by following the steps from the second subsection.

### Variant 1: Building using the provided scripts
Pie can be built easily by simply running the OS-specific script from the `build/` folder. This will first compile the project stored inside `piejfx-plugin-core/`, install it to the local repository, then it will build the contents of `piejfx/` and, in the end, will run `jpackage` on the generated artifact and its dependencies.

### Variant 2: Building manually

#### Step 1: Build the `piejfx-plugin-core` project first, then add it to the local Maven repository
This can be done by running the `mvn install` inside the `piejfx-plugin-core/` folder.

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

## Extending functionality by developing custom plugins

### Introduction
Plugin development is done by using the `piejfx-plugin-sdk`, which is a pre-made Maven project containing JavaFX and a reference to the `plugin/` dependency.

> **Note:** Make sure you run `mvn install` on the `piejfx-plugin-core` first, as it is a dependency of the sdk. Installing it will put in inside your local repository. This needs to be done, because piejfx is not part of any public Maven repository yet.

### Hooks and the `Plugin` interface

```java
public interface Plugin {
    void onLoad(PluginContext var1);
    void onKeyPress(KeyEvent var1);
    void onSaveFile(File var1);
    void onOpenFile(File var1);
    void onCreateFile(File var1);
    void onCreateFolder(File var1);
    void onDeleteFile(File var1);
}
```

Every plugin will implement the `Plugin` interface, as shown in the example from the pre-made plugin SDK. This interface has certain methods (called "hooks") that are triggered whenever an action happens inside piejfx. Some examples include `onKeyPress` (which calls the plugin's handler whenever a key is pressed, together with a reference to the JavaFX `KeyEvent`), `onCreateFile` (which sends an object reference to the file that was created) and, the most important one, `onLoad`, which sends a reference to piejfx's accessible user interface components and some useful services.

#### The `onLoad` hook
piejfx calls the plugin's `onLoad` method with a `PluginContext` object as a parameter. The context variable contains references to the sidebar tab pane, editor tab pane, and to the menu bar. The context also references pre-made services such as the one that displays notifications and the one that opens a folder in the sidebar, in order to improve usability and define a standard on these tasks.

### Creating a custom UI for your plugin
The plugin SDK depends on JavaFX, meaning that anyone with little to no JavaFX knowledge is able to add their custom interface to it. This could be done by manually modifying the components sent through the `PluginContext` (e.g. adding a new tab to the sidebar tab pane or modifying an existing one - which could conflict with other plugins, however). The SDK provides examples on how to do that in the `ExamplePlugin` class.

### Where do I store the plugin data?

#### Storing the plugin artifact
Plugins will be stored inside the user's application data folder (e.g. for Windows `AppData\Local\bwxor\piejfx\<VERSION>\plugins`). Inside the plugins directory, a new directory will be created (name doesn't matter), containing the plugin artifact (`.jar` file).

#### Storing the dependencies
Dependencies should be stored in a separate `deps/` folder, which is located under the same level as the plugin jar. The `deps/` folder should containg everything that's included as a Maven dependency inside your plugin's `pom.xml` file. Such a dependency folder can be generated by simply running `mvn dependency:copy-dependencies -DoutputDirectory=<DIRECTORY_NAME>` inside your project directory and copying the deps folder to the artifact's directory.

#### Additional configuration files
It is common for plugins to persist their state in configuration files. `PluginContext` contains a field `configurationDirectoryPath`, that refers to the `config/` directory, on the same level as the plugin jar file. Creating the directory is entirely optional. Some plugins don't need configuration data.

#### How does the final artifact need to look like?
Plugins could be sent through `zip` archives, containing a single folder with (recommended) the plugin name. Inside that folder should reside the plugin jar file, togetheer with the `config/` and `deps/` directories. 

##### Quick example
For a plugin called `sample-plugin`, it can be stored under `plugins\sample-plugin\sample-plugin.jar`. Its dependencies (also jar files) will be stored under `plugins\sample-plugin\deps\`.

```
sample-plugin/
├── deps/
│   ├── javafx-base-21.0.6.jar
│   ├── javafx-controls-21.0.6.jar
├── config/
│   └── user-data.json
└── sample-plugin.jar
``` 




