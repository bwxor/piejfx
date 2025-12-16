package com.bwxor.piejfx.utility;

import com.bwxor.piejfx.state.FolderTreeViewState;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.io.File;

public class FolderTreeViewUtility {
    public static void toggleFolderTreeView(SplitPane horizontalSplitPane, TreeView folderTreeView) {
        folderTreeView.setRoot(createTreeItem());

        if (horizontalSplitPane.getItems().contains(folderTreeView)) {
            horizontalSplitPane.getItems().remove(folderTreeView);
        } else {
            horizontalSplitPane.getItems().addFirst(folderTreeView);
        }
    }

    public static TreeItem createTreeItem() {
        File rootFile = FolderTreeViewState.instance.getOpenedFolder();

        TreeItem parent = new TreeItem();
        createTreeItem(rootFile, parent);
        return (TreeItem)parent.getChildren().get(0);
    }

    private static void createTreeItem(File rootFile, TreeItem parent) {
        if (rootFile.isDirectory()) {
            TreeItem node = new TreeItem(rootFile.getName());
            parent.getChildren().add(node);
            for (File f: rootFile.listFiles()) {

                TreeItem placeholder = new TreeItem();
                node.getChildren().add(placeholder);

                node.addEventHandler(TreeItem.branchExpandedEvent(), new EventHandler() {
                    @Override
                    public void handle(Event event) {
                        createTreeItem(f, node);
                        node.getChildren().remove(placeholder);
                        node.removeEventHandler(TreeItem.branchExpandedEvent(), this);
                    }
                });

            }
        } else {
            parent.getChildren().add(new TreeItem(rootFile.getName()));
        }
    }
}
