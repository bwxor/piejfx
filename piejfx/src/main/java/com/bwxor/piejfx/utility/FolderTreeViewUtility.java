package com.bwxor.piejfx.utility;

import com.bwxor.piejfx.state.FolderTreeViewState;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.*;

import java.io.File;

public class FolderTreeViewUtility {
    public static void showFolderTreeView(SplitPane horizontalSplitPane, TreeView folderTreeView, SplitPane verticalSplitPane, TabPane editorTabPane, TabPane terminalTabPane, Label titleBarLabel) {
        folderTreeView.setRoot(createTreeItem(verticalSplitPane, editorTabPane, terminalTabPane, titleBarLabel));

        if (!horizontalSplitPane.getItems().contains(folderTreeView)) {
            toggleFolderTreeView(horizontalSplitPane, folderTreeView, verticalSplitPane, editorTabPane, terminalTabPane, titleBarLabel);
        }
    }

    public static void toggleFolderTreeView(SplitPane horizontalSplitPane, TreeView folderTreeView, SplitPane verticalSplitPane, TabPane editorTabPane, TabPane terminalTabPane, Label titleBarLabel) {
        folderTreeView.setRoot(createTreeItem(verticalSplitPane, editorTabPane, terminalTabPane, titleBarLabel));

        if (horizontalSplitPane.getItems().contains(folderTreeView)) {
            horizontalSplitPane.getItems().remove(folderTreeView);
        } else {
            horizontalSplitPane.getItems().addFirst(folderTreeView);
        }
    }

    public static TreeItem createTreeItem(SplitPane verticalSplitPane, TabPane editorTabPane, TabPane terminalTabPane, Label titleBarLabel) {
        File rootFile = FolderTreeViewState.instance.getOpenedFolder();

        TreeItem parent = new TreeItem();
        createTreeItem(rootFile, parent, verticalSplitPane, editorTabPane, terminalTabPane, titleBarLabel);
        return (TreeItem) parent.getChildren().getFirst();
    }

    private static void createTreeItem(File rootFile, TreeItem parent, SplitPane verticalSplitPane, TabPane editorTabPane, TabPane terminalTabPane, Label titleBarLabel) {
        if (rootFile.isDirectory()) {
            TreeItem node = new TreeItem("\uD83D\uDCC1 " + rootFile.getName());
            parent.getChildren().add(node);
            for (File f : rootFile.listFiles()) {
                TreeItem placeholder = new TreeItem();
                node.getChildren().add(placeholder);

                node.addEventHandler(TreeItem.branchExpandedEvent(), new EventHandler() {
                    @Override
                    public void handle(Event event) {
                        createTreeItem(f, node, verticalSplitPane, editorTabPane, terminalTabPane, titleBarLabel);
                        node.getChildren().remove(placeholder);
                        node.removeEventHandler(TreeItem.branchExpandedEvent(), this);

                        node.getChildren().sort(
                                (Object t1, Object t2) -> {
                                    if (((TreeItem) t1).getChildren().size() > 0) {
                                        return -1;
                                    } else {
                                        return 1;
                                    }
                                }
                        );
                    }
                });
            }
        } else {
            TreeItem treeItem = new TreeItem(rootFile.getName());
            parent.getChildren().add(treeItem);
        }
    }
}
