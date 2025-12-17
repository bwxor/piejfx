package com.bwxor.piejfx.utility;

import com.bwxor.piejfx.control.FileTreeItem;
import com.bwxor.piejfx.state.FolderTreeViewState;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.File;

public class FolderTreeViewUtility {
    private static boolean firstLaunch = true;

    public static void showFolderTreeView(SplitPane horizontalSplitPane, TreeView folderTreeView, SplitPane verticalSplitPane, TabPane editorTabPane, TabPane terminalTabPane, Label titleBarLabel) {
        if (firstLaunch) {
            folderTreeView.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if (folderTreeView.getSelectionModel().getSelectedIndex() > 0) {
                    if (e.getClickCount() == 2) {
                        File file = ((FileTreeItem) folderTreeView.getSelectionModel().getSelectedItem()).getFile();

                        if (!file.isDirectory()) {
                            OpenFileUtility.openFile(horizontalSplitPane, verticalSplitPane, folderTreeView, editorTabPane, terminalTabPane, titleBarLabel, file);
                        }
                    }
                }
            });
            firstLaunch = false;
        }

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
            horizontalSplitPane.setDividerPosition(0, 0.25);
        }
    }

    public static TreeItem createTreeItem(SplitPane verticalSplitPane, TabPane editorTabPane, TabPane terminalTabPane, Label titleBarLabel) {
        File rootFile = FolderTreeViewState.instance.getOpenedFolder();

        FileTreeItem parent = new FileTreeItem();
        createTreeItem(rootFile, parent, verticalSplitPane, editorTabPane, terminalTabPane, titleBarLabel);
        return parent.getChildren().getFirst();
    }

    private static void createTreeItem(File rootFile, TreeItem parent, SplitPane verticalSplitPane, TabPane editorTabPane, TabPane terminalTabPane, Label titleBarLabel) {
        if (rootFile.isDirectory()) {
            TreeItem node = new FileTreeItem("\uD83D\uDCC1 " + rootFile.getName(), rootFile);
            parent.getChildren().add(node);
            for (File f : rootFile.listFiles()) {
                TreeItem placeholder = new FileTreeItem();
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
            FileTreeItem treeItem = new FileTreeItem(rootFile.getName(), rootFile);
            parent.getChildren().add(treeItem);
        }
    }
}
