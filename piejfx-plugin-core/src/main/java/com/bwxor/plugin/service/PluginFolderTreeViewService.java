package com.bwxor.plugin.service;

import javafx.scene.control.TreeItem;

public interface PluginFolderTreeViewService {
    void showFolderTreeView();
    void toggleFolderTreeView();
    TreeItem createTreeItem();
}