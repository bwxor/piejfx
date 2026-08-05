package com.bwxor.piejfx.dto;

import com.bwxor.plugin.Plugin;

import java.net.URLClassLoader;
import java.nio.file.Path;

public class LoadedPlugin {
    private String name;
    private Path directory;
    private Plugin hook;
    private URLClassLoader classLoader;
    private boolean enabled;

    public LoadedPlugin(String name, Path directory, Plugin hook, URLClassLoader classLoader, boolean enabled) {
        this.name = name;
        this.directory = directory;
        this.hook = hook;
        this.classLoader = classLoader;
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Path getDirectory() {
        return directory;
    }

    public void setDirectory(Path directory) {
        this.directory = directory;
    }

    public Plugin getHook() {
        return hook;
    }

    public void setHook(Plugin hook) {
        this.hook = hook;
    }

    public URLClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(URLClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getSlug() {
        return directory.getFileName().toString();
    }
}
