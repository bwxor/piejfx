package com.bwxor.piejfx.state;

import com.bwxor.piejfx.dto.FetchedPlugin;

import java.util.ArrayList;
import java.util.List;

public class FetchedPluginsState {
    private List<FetchedPlugin> plugins = new ArrayList<>();
    public static final FetchedPluginsState instance = new FetchedPluginsState();

    private FetchedPluginsState() {
    }

    public List<FetchedPlugin> getPlugins() {
        return plugins;
    }

    public void setPlugins(List<FetchedPlugin> plugins) {
        this.plugins = plugins;
    }
}
