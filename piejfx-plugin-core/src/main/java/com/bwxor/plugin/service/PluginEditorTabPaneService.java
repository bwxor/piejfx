package com.bwxor.plugin.service;

import com.bwxor.plugin.type.RemoveSelectedTabFromPaneOption;

import java.io.File;

public interface PluginEditorTabPaneService {
    void addTabToPane(File file);
    void addTabToPane(String tabTitle);
    RemoveSelectedTabFromPaneOption removeSelectedTabFromPane();
}
