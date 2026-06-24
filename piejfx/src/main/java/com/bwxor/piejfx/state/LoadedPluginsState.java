package com.bwxor.piejfx.state;

import com.bwxor.piejfx.dto.LoadedPlugin;

import java.util.ArrayList;
import java.util.List;

public class LoadedPluginsState {
    private List<LoadedPlugin> plugins = new ArrayList<>();
    public static final LoadedPluginsState instance = new LoadedPluginsState();

    private LoadedPluginsState() {
    }

    public List<LoadedPlugin> getPlugins() {
        return plugins;
    }

    public void setPlugins(List<LoadedPlugin> plugins) {
        this.plugins = plugins;
    }
}
