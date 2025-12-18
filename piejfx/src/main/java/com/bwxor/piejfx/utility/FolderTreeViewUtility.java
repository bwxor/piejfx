package com.bwxor.piejfx.utility;

import com.bwxor.piejfx.control.FileTreeItem;
import com.bwxor.piejfx.dto.TreeViewStructure;
import com.bwxor.piejfx.state.FolderTreeViewState;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.util.ArrayList;

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

        FolderTreeViewState.instance.setTreeViewStructure(new TreeViewStructure());
        fillExpansionState(FolderTreeViewState.instance.getTreeViewStructure(), folderTreeView.getRoot());
        folderTreeView.setRoot(createTreeItem(verticalSplitPane, editorTabPane, terminalTabPane, titleBarLabel));

        if (!horizontalSplitPane.getItems().contains(folderTreeView)) {
            horizontalSplitPane.getItems().addFirst(folderTreeView);
            horizontalSplitPane.setDividerPosition(0, 0.25);
        }

        if (FolderTreeViewState.instance.getTreeViewStructure() != null) {
            fillTreeViewWithExpansionState(FolderTreeViewState.instance.getTreeViewStructure(), folderTreeView.getRoot());
        }
        folderTreeView.getRoot().setExpanded(true);
    }

    public static void toggleFolderTreeView(SplitPane horizontalSplitPane, TreeView folderTreeView, SplitPane verticalSplitPane, TabPane editorTabPane, TabPane terminalTabPane, Label titleBarLabel) {
        if (horizontalSplitPane.getItems().contains(folderTreeView)) {
            FolderTreeViewState.instance.setTreeViewStructure(new TreeViewStructure());
            fillExpansionState(FolderTreeViewState.instance.getTreeViewStructure(), folderTreeView.getRoot());
            horizontalSplitPane.getItems().remove(folderTreeView);
        } else {
            folderTreeView.setRoot(createTreeItem(verticalSplitPane, editorTabPane, terminalTabPane, titleBarLabel));
            horizontalSplitPane.getItems().addFirst(folderTreeView);
            horizontalSplitPane.setDividerPosition(0, 0.25);

            if (FolderTreeViewState.instance.getTreeViewStructure() != null) {
                fillTreeViewWithExpansionState(FolderTreeViewState.instance.getTreeViewStructure(), folderTreeView.getRoot());
            }
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

    private static void fillExpansionState(TreeViewStructure treeViewStructure, TreeItem treeItem) {
        if (treeItem == null) {
            return;
        }

        treeViewStructure.setChildren(new ArrayList<>());

        for (int i = 0; i < treeItem.getChildren().size(); i++) {
            TreeItem child = (TreeItem) treeItem.getChildren().get(i);
            TreeViewStructure newItem = new TreeViewStructure();
            newItem.setFile(((FileTreeItem) child).getFile());
            if (child.isExpanded()) {
                newItem.setExpanded(true);
                treeViewStructure.getChildren().add(newItem);
                fillExpansionState(newItem, child);
            }
        }
    }

    private static void fillTreeViewWithExpansionState(TreeViewStructure treeViewStructure, TreeItem treeItem) {
        ObservableList<TreeItem> children = treeItem.getChildren();

        if (treeViewStructure.getChildren() != null) {
            treeItem.setExpanded(true);

            for (int i = 0; i < children.size(); i++) {
                for (TreeViewStructure s : treeViewStructure.getChildren()) {
                    FileTreeItem f = (FileTreeItem) children.get(i);
                    if (f.getFile() != null && f.getFile().equals(s.getFile())
                            && s.isExpanded()) {
                        children.get(i).setExpanded(true);
                        fillTreeViewWithExpansionState(s, children.get(i));
                    }
                }
            }
        }
    }
}
