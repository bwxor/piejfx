package com.bwxor.piejfx.dto;

import com.bwxor.plugin.Plugin;

public class LoadedPlugin {
    private String name;
    private Plugin hook;

    public LoadedPlugin(String name, Plugin hook) {
        this.name = name;
        this.hook = hook;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Plugin getHook() {
        return hook;
    }

    public void setHook(Plugin hook) {
        this.hook = hook;
    }
}
