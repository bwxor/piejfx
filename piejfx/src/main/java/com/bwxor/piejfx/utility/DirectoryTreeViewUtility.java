package com.bwxor.piejfx.utility;

import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeView;

public class DirectoryTreeViewUtility {
    public static void toggleDirectoryTreeView(SplitPane horizontalSplitPane, TreeView<String> folderTreeView) {
        if (horizontalSplitPane.getItems().contains(folderTreeView)) {
            horizontalSplitPane.getItems().remove(folderTreeView);
        } else {
            horizontalSplitPane.getItems().addFirst(folderTreeView);
        }
    }
}
