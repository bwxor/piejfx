package com.bwxor.piejfx.dto;

import com.bwxor.plugin.Plugin;

import java.net.URLClassLoader;
import java.nio.file.Path;

public class LoadedPlugin {
    private String name;
    private Path directory;
    private Plugin hook;
    private URLClassLoader classLoader;

    public LoadedPlugin(String name, Path directory, Plugin hook, URLClassLoader classLoader) {
        this.name = name;
        this.directory = directory;
        this.hook = hook;
        this.classLoader = classLoader;
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
}
