package com.bwxor.piejfx.dto;

import com.bwxor.plugin.Plugin;

import java.nio.file.Path;

public class LoadedPlugin {
    private String name;
    private Path directory;
    private Plugin hook;

    public LoadedPlugin(String name, Path directory, Plugin hook) {
        this.name = name;
        this.directory = directory;
        this.hook = hook;
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
}
