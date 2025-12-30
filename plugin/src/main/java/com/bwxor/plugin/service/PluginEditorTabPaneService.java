package com.bwxor.plugin.service;

import java.io.File;

public interface PluginEditorTabPaneService {
    void addTabToPane(File file);
    void addTabToPane(String tabTitle);
    RemoveSelectedTabFromPaneResponse removeSelectedTabFromPane();
}
