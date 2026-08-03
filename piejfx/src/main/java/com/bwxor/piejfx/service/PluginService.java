package com.bwxor.piejfx.service;

import com.bwxor.piejfx.constants.AppDirConstants;
import com.bwxor.piejfx.dto.LoadedPlugin;
import com.bwxor.piejfx.state.*;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

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
                        var pluginClassLoader = new URLClassLoader(Stream.concat(Arrays.stream(urls), Arrays.stream(new URL[]{jar.toURI().toURL()})).toArray(URL[]::new), getClass().getClassLoader());

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
        File depsDirectory = new File(Paths.get(pluginDirectory.getPath(), "deps").toUri());

        URL[] urls = new URL[0];

        if (depsDirectory.exists()) {

            List<File> depFiles = Arrays.stream(Objects.requireNonNull(depsDirectory.listFiles()))
                    .filter(e -> e.getName().endsWith(".jar")).toList();

            urls = depFiles.stream().map(
                            f -> {
                                try {
                                    return f.toURI().toURL();
                                } catch (MalformedURLException ex) {
                                    return null;
                                }
                            }).filter(Objects::nonNull)
                    .toArray(URL[]::new);
        }

        return urls;
    }

    private LoadedPlugin toPlugin(File pluginDirectory, File f, URLClassLoader classLoader) {
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

            return new LoadedPlugin(pluginName, pluginDirectory.toPath(), p, classLoader);
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

    private Set<Class> getClasses(JarFile jarFile, Set<String> classNames, URLClassLoader classLoader) throws ClassNotFoundException, MalformedURLException {
        Set<Class> classes = new HashSet<>();

        var cl = new URLClassLoader(new URL[]{new File(jarFile.getName()).toURI().toURL()}, classLoader);
        for (String name : classNames) {
            if (!name.equals("module-info")) {
                classes.add(cl.loadClass(name));
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
            invokeOnLoadIndividually(p);
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
        LoadedPluginsState.instance.getPlugins()
                .forEach(
                        e -> invokeWithPluginClassLoader(e, () -> e.getHook().onKeyPress(k))
                );
    }

    public void invokeOnSaveFile(File file) {
        LoadedPluginsState.instance.getPlugins()
                .forEach(
                        e -> invokeWithPluginClassLoader(e, () -> e.getHook().onSaveFile(file))
                );
    }

    public void invokeOnOpenFile(File file) {
        LoadedPluginsState.instance.getPlugins()
                .forEach(
                        e -> invokeWithPluginClassLoader(e, () -> e.getHook().onOpenFile(file))
                );
    }

    public void invokeOnOpenFolder(File file) {
        LoadedPluginsState.instance.getPlugins()
                .forEach(
                        e -> invokeWithPluginClassLoader(e, () -> e.getHook().onOpenFolder(file))
                );
    }

    public void invokeOnCreateFile(File file) {
        LoadedPluginsState.instance.getPlugins()
                .forEach(
                        e -> invokeWithPluginClassLoader(e, () -> e.getHook().onCreateFile(file))
                );
    }

    public void invokeOnCreateFolder(File file) {
        LoadedPluginsState.instance.getPlugins()
                .forEach(
                        e -> invokeWithPluginClassLoader(e, () -> e.getHook().onCreateFolder(file))
                );
    }

    public void invokeOnRenameFile(File file) {
        LoadedPluginsState.instance.getPlugins()
                .forEach(
                        e -> invokeWithPluginClassLoader(e, () -> e.getHook().onRenameFile(file))
                );
    }

    public void invokeOnDeleteFile(File file) {
        LoadedPluginsState.instance.getPlugins()
                .forEach(
                        e -> invokeWithPluginClassLoader(e, () -> e.getHook().onDeleteFile(file))
                );
    }

    public void invokeOnThemeChange(URL url) {
        LoadedPluginsState.instance.getPlugins()
                .forEach(
                        e -> invokeWithPluginClassLoader(e, () -> e.getHook().onThemeChange(url))
                );
    }
}