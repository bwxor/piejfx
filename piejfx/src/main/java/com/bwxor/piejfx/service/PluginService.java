package com.bwxor.piejfx.service;

import com.bwxor.piejfx.constants.AppDirConstants;
import com.bwxor.piejfx.dto.LoadedPlugin;
import com.bwxor.piejfx.state.*;
import com.bwxor.piejfx.state.ServiceState;
import com.bwxor.plugin.Plugin;
import com.bwxor.plugin.input.ApplicationWindow;
import com.bwxor.plugin.input.PluginContext;
import com.bwxor.plugin.input.ServiceContainer;
import com.bwxor.plugin.input.Stylesheets;
import javafx.scene.input.KeyEvent;
import org.json.JSONObject;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

class PluginClassLoader extends URLClassLoader {
    static { ClassLoader.registerAsParallelCapable(); }

    PluginClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // These packages must come from the shared parent so that cast checks work
        // across the host↔plugin boundary.
        if (name.startsWith("com.bwxor.plugin.")
                || name.startsWith("java.")
                || name.startsWith("javax.")
                || name.startsWith("sun.")
                || name.startsWith("jdk.")
                || name.startsWith("javafx.")
                || name.startsWith("com.sun.javafx.")
                || name.startsWith("com.sun.glass.")
                || name.startsWith("com.sun.prism.")
                || name.startsWith("com.sun.scenario.")) {
            return super.loadClass(name, resolve);
        }

        synchronized (getClassLoadingLock(name)) {
            // Return already-loaded class if present in this loader.
            Class<?> c = findLoadedClass(name);
            if (c != null) {
                if (resolve) resolveClass(c);
                return c;
            }

            // Try the plugin's own URLs first (deps + plugin jar).
            try {
                c = findClass(name);
                if (resolve) resolveClass(c);
                return c;
            } catch (ClassNotFoundException ignored) {
                // Not in the plugin — fall through to parent.
            }

            return super.loadClass(name, resolve);
        }
    }
}

public class PluginService {
    public List<LoadedPlugin> getPlugins() {
        List<LoadedPlugin> loadedPlugins = new ArrayList<>();

        File[] pluginDir = new File(AppDirConstants.PLUGINS_DIR.toUri()).listFiles();

        if (pluginDir != null) {
            var individualPlugins = Stream.of(pluginDir)
                    .filter(file -> file.isDirectory())
                    .toList();

            for (File directory : individualPlugins) {
                var pluginJars = Arrays.stream(directory.listFiles()).filter(f -> !f.isDirectory() && f.getName().endsWith(".jar")).findFirst();

                if (pluginJars.isEmpty()) {
                    ServiceState.instance.getNotificationService().showNotificationOk("Couldn't find any jar file inside " + directory.getName() + ".");
                } else {
                    var jar = pluginJars.get();

                    try {
                        var urls = loadPluginDependencies(directory);
                        var pluginClassLoader = new PluginClassLoader(Stream.concat(Arrays.stream(urls), Arrays.stream(new URL[]{jar.toURI().toURL()})).toArray(URL[]::new), getClass().getClassLoader());

                        var plugin = toPlugin(directory, jar, pluginClassLoader);
                        if (plugin != null) {
                            loadedPlugins.add(plugin);
                        }
                    } catch (MalformedURLException e) {
                        ServiceState.instance.getNotificationService().showNotificationOk("Couldn't load plugin " + jar.getName() + ".");
                    }
                }
            }
        }


        return loadedPlugins;
    }

    public URL[] loadPluginDependencies(File pluginDirectory) {
        Path depsDirectory = pluginDirectory.toPath().resolve("deps");
        List<URL> urls = new ArrayList<>();

        if (Files.exists(depsDirectory) && Files.isDirectory(depsDirectory)) {
            try (var stream = Files.list(depsDirectory)) {
                List<Path> depFiles = stream
                        .filter(path -> path.toString().toLowerCase().endsWith(".jar"))
                        .toList();

                for (Path depFile : depFiles) {
                    try {
                        Path tempDep = Files.createTempFile("plugin-dep-", ".jar");
                        tempDep.toFile().deleteOnExit(); // Clean up on JVM exit

                        Files.copy(depFile, tempDep, StandardCopyOption.REPLACE_EXISTING);

                        urls.add(tempDep.toUri().toURL());
                    } catch (IOException e) {
                        System.err.println("Failed to shadow-copy dependency: " + depFile.getFileName() + " - " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                System.err.println("Failed to read deps directory: " + e.getMessage());
            }
        }

        return urls.toArray(new URL[0]);
    }

    private LoadedPlugin toPlugin(File pluginDirectory, File f, PluginClassLoader classLoader) {
        try (JarFile jarFile = new JarFile(f)) {
            String pluginName = getPluginName(jarFile);
            Set<String> classNames = getClassNames(jarFile);
            Set<Class> classes = getClasses(jarFile, classNames, classLoader);

            var classesThatImplementPlugin = classes.stream().filter(
                    Plugin.class::isAssignableFrom
            ).toList();

            if (classesThatImplementPlugin.size() != 1) {
                ServiceState.instance.getNotificationService().showNotificationOk("Plugins should have exactly one class that implements the Plugin interface.");
                return null;
            }

            Class<? extends Plugin> pluginClass = classesThatImplementPlugin.getFirst().asSubclass(Plugin.class);
            Plugin p = pluginClass.getDeclaredConstructor().newInstance();

            String slug = pluginDirectory.getName();
            boolean enabled = ServiceState.instance.getPluginEnabledConfigService().isEnabled(slug);

            return new LoadedPlugin(pluginName, pluginDirectory.toPath(), p, classLoader, enabled);
        } catch (IOException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                 InstantiationException | IllegalAccessException ex) {
            ServiceState.instance.getNotificationService().showNotificationOk("Problem encountered while reading the plugin files. Some bad configuration may cause this.");
            return null;
        }
    }

    private String getPluginName(JarFile jarFile) throws IOException {
        Enumeration<JarEntry> e = jarFile.entries();
        while (e.hasMoreElements()) {
            JarEntry jarEntry = e.nextElement();
            if (jarEntry.getName().equals("plugin.json")) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(jarFile.getInputStream(jarEntry)));
                String rawContent = bufferedReader.readAllAsString();
                JSONObject jsonObject = new JSONObject(rawContent);
                bufferedReader.close();
                return jsonObject.getString("name");
            }
        }

        return null;
    }

    private Set<String> getClassNames(JarFile jarFile) throws IOException {
        Set<String> classNames = new HashSet<>();
        Enumeration<JarEntry> e = jarFile.entries();
        while (e.hasMoreElements()) {
            JarEntry jarEntry = e.nextElement();
            if (jarEntry.getName().endsWith(".class")) {
                String className = jarEntry.getName()
                        .replace("/", ".")
                        .replace(".class", "");
                classNames.add(className);
            }
        }
        return classNames;
    }

    private Set<Class> getClasses(JarFile jarFile, Set<String> classNames, PluginClassLoader classLoader) throws ClassNotFoundException {
        Set<Class> classes = new HashSet<>();

        // The plugin jar is already included in classLoader's URLs — no extra
        // nested URLClassLoader needed here.
        for (String name : classNames) {
            if (!name.equals("module-info")) {
                classes.add(classLoader.loadClass(name));
            }
        }

        return classes;
    }

    /**
     * Runs a plugin callback with the thread context classloader set to the
     * plugin's own classloader. This is required so that ServiceLoader-based
     * discovery inside plugin dependencies (e.g. Maven's XmlService, SLF4J
     * providers, JDBC drivers) can find implementations bundled with the plugin.
     */
    private void invokeWithPluginClassLoader(LoadedPlugin p, Runnable callback) {
        Thread currentThread = Thread.currentThread();
        ClassLoader original = currentThread.getContextClassLoader();
        try {
            currentThread.setContextClassLoader(p.getHook().getClass().getClassLoader());
            callback.run();
        } finally {
            currentThread.setContextClassLoader(original);
        }
    }

    public void invokeOnLoad() {
        for (LoadedPlugin p : LoadedPluginsState.instance.getPlugins()) {
            if (p.isEnabled()) {
                invokeOnLoadIndividually(p);
            }
        }
    }

    public void invokeOnLoadIndividually(LoadedPlugin p) {
        HostServicesState hostServicesState = HostServicesState.instance;

        ApplicationWindow applicationWindow = new ApplicationWindow();
        applicationWindow.setSidebarTabPane(UIState.instance.getSplitTabPane());
        applicationWindow.setEditorTabPane(UIState.instance.getEditorTabPane());
        applicationWindow.setMenuBar(UIState.instance.getMenuBar());

        ServiceContainer serviceContainer = new ServiceContainer(
                ServiceState.instance.getStartStopService(),
                ServiceState.instance.getEditorTabPaneService(),
                ServiceState.instance.getFolderTreeViewService(),
                ServiceState.instance.getNotificationService(),
                ServiceState.instance.getFileService(),
                ServiceState.instance.getTerminalTabPaneService()
        );

        Path configurationDirectoryPath = Paths.get(AppDirConstants.PLUGINS_DIR.toString(), p.getDirectory().getFileName().toString(), "config");

        URL defaultStylesheet, defaultMaximizedStylesheet;
        try {
            defaultStylesheet = AppDirConstants.DEFAULT_STYLES_FILE.toUri().toURL();
            defaultMaximizedStylesheet = AppDirConstants.DEFAULT_MAXIMIZED_STYLES_FILE.toUri().toURL();
        } catch(MalformedURLException ex) {
            defaultStylesheet = defaultMaximizedStylesheet = null;
        }

        PluginContext pluginContext = new PluginContext(
                applicationWindow,
                serviceContainer,
                configurationDirectoryPath,
                hostServicesState.getHostServices(),
                new Stylesheets(
                        ThemeState.instance.getCurrentTheme().getUrl(),
                        defaultStylesheet,
                        defaultMaximizedStylesheet
                )
        );
        invokeWithPluginClassLoader(p, () -> p.getHook().onLoad(pluginContext));
    }

    public void invokeOnKeyPress(KeyEvent k) {
        LoadedPluginsState.instance.getPlugins().stream()
                .filter(LoadedPlugin::isEnabled)
                .forEach(e -> invokeWithPluginClassLoader(e, () -> e.getHook().onKeyPress(k)));
    }

    public void invokeOnSaveFile(File file) {
        LoadedPluginsState.instance.getPlugins().stream()
                .filter(LoadedPlugin::isEnabled)
                .forEach(e -> invokeWithPluginClassLoader(e, () -> e.getHook().onSaveFile(file)));
    }

    public void invokeOnOpenFile(File file) {
        LoadedPluginsState.instance.getPlugins().stream()
                .filter(LoadedPlugin::isEnabled)
                .forEach(e -> invokeWithPluginClassLoader(e, () -> e.getHook().onOpenFile(file)));
    }

    public void invokeOnOpenFolder(File file) {
        LoadedPluginsState.instance.getPlugins().stream()
                .filter(LoadedPlugin::isEnabled)
                .forEach(e -> invokeWithPluginClassLoader(e, () -> e.getHook().onOpenFolder(file)));
    }

    public void invokeOnCreateFile(File file) {
        LoadedPluginsState.instance.getPlugins().stream()
                .filter(LoadedPlugin::isEnabled)
                .forEach(e -> invokeWithPluginClassLoader(e, () -> e.getHook().onCreateFile(file)));
    }

    public void invokeOnCreateFolder(File file) {
        LoadedPluginsState.instance.getPlugins().stream()
                .filter(LoadedPlugin::isEnabled)
                .forEach(e -> invokeWithPluginClassLoader(e, () -> e.getHook().onCreateFolder(file)));
    }

    public void invokeOnRenameFile(File file) {
        LoadedPluginsState.instance.getPlugins().stream()
                .filter(LoadedPlugin::isEnabled)
                .forEach(e -> invokeWithPluginClassLoader(e, () -> e.getHook().onRenameFile(file)));
    }

    public void invokeOnDeleteFile(File file) {
        LoadedPluginsState.instance.getPlugins().stream()
                .filter(LoadedPlugin::isEnabled)
                .forEach(e -> invokeWithPluginClassLoader(e, () -> e.getHook().onDeleteFile(file)));
    }

    public void invokeOnThemeChange(URL url) {
        LoadedPluginsState.instance.getPlugins().stream()
                .filter(LoadedPlugin::isEnabled)
                .forEach(e -> invokeWithPluginClassLoader(e, () -> e.getHook().onThemeChange(url)));
    }
}