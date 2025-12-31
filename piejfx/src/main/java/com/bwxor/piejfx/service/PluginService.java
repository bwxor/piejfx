package com.bwxor.piejfx.service;

import com.bwxor.piejfx.constants.AppDirConstants;
import com.bwxor.piejfx.dto.LoadedPlugin;
import com.bwxor.piejfx.state.ServiceState;
import com.bwxor.plugin.Plugin;
import org.json.JSONObject;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public class PluginService {
    public List<LoadedPlugin> getPlugins() {
        List<LoadedPlugin> loadedPlugins = new ArrayList<>();

        File[] pluginDir = new File(AppDirConstants.PLUGINS_DIR.toUri()).listFiles();

        if (pluginDir != null) {
            var files = Stream.of(pluginDir)
                    .filter(file -> !file.isDirectory())
                    .filter(file -> file.getName().endsWith(".jar"))
                    .toList();

            for (File f : files) {
                var plugin = toPlugin(f);
                if (plugin != null) {
                    loadedPlugins.add(toPlugin(f));
                }
            }
        }


        return loadedPlugins;
    }

    private LoadedPlugin toPlugin(File f) {
        try (JarFile jarFile = new JarFile(f)) {
            String pluginName = getPluginName(jarFile);
            Set<String> classNames = getClassNames(jarFile);
            Set<Class> classes = getClasses(jarFile, classNames);

            var classesThatImplementPlugin = classes.stream().filter(
                    Plugin.class::isAssignableFrom
            ).toList();

            if (classesThatImplementPlugin.size() != 1) {
                ServiceState.getInstance().getNotificationService().showNotificationOk("Plugins should have exactly one class that implements the Plugin interface.");
                return null;
            }

            Class<? extends Plugin> pluginClass = classesThatImplementPlugin.getFirst().asSubclass(Plugin.class);
            Plugin p = pluginClass.getDeclaredConstructor().newInstance();

            return new LoadedPlugin(pluginName, p);
        } catch (IOException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                 InstantiationException | IllegalAccessException ex) {
            ServiceState.getInstance().getNotificationService().showNotificationOk("Problem encountered while reading the plugin files. Some bad configuration may cause this.");
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

    private Set<Class> getClasses(JarFile jarFile, Set<String> classNames) throws ClassNotFoundException, MalformedURLException {
        Set<Class> classes = new HashSet<>();

        var cl = URLClassLoader.newInstance(new URL[]{new URL("jar:file:" + jarFile + "!/")});
        for (String name : classNames) {
            classes.add(cl.loadClass(name));
        }

        return classes;
    }
}
