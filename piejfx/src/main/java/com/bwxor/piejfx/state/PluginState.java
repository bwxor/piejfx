package com.bwxor.piejfx.state;

import com.bwxor.piejfx.dto.LoadedPlugin;
import com.bwxor.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class PluginState {
    private List<LoadedPlugin> plugins = new ArrayList<>();
    public static PluginState instance = new PluginState();

    private PluginState() {
    }

    public List<LoadedPlugin> getPlugins() {
        return plugins;
    }

    public void setPlugins(List<LoadedPlugin> plugins) {
        this.plugins = plugins;
    }

    public static PluginState getInstance() {
        return instance;
    }
}
