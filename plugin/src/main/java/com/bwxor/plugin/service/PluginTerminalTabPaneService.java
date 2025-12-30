package com.bwxor.plugin.service;

import javafx.scene.control.TabPane;

public interface PluginTerminalTabPaneService {
    void addTabToPane(String process);
    void removeSelectedTabFromPane(TabPane tabPane);
    void toggleTerminalTabPane();
}