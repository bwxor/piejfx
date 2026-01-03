package com.bwxor.piejfx.service;

import com.bwxor.piejfx.control.FileTreeItem;
import com.bwxor.piejfx.dto.TreeViewStructure;
import com.bwxor.piejfx.state.*;
import com.bwxor.plugin.service.PluginFolderTreeViewService;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.util.ArrayList;

public class FolderTreeViewService implements PluginFolderTreeViewService {
    private boolean firstLaunch = true;

    public void showFolderTreeView() {
        UIState uiState = UIState.instance;
        ServiceState serviceState = ServiceState.instance;

        if (firstLaunch) {
            uiState.getFolderTreeView().addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if (uiState.getFolderTreeView().getSelectionModel().getSelectedIndex() > 0) {
                    if (e.getClickCount() == 2) {
                        File file = ((FileTreeItem) uiState.getFolderTreeView().getSelectionModel().getSelectedItem()).getFile();

                        if (!file.isDirectory()) {
                            serviceState.getFileService().openFile(file);
                        }
                    }
                }
            });
            firstLaunch = false;
        }

        FolderTreeViewState.instance.setTreeViewStructure(new TreeViewStructure());
        fillExpansionState(FolderTreeViewState.instance.getTreeViewStructure(), uiState.getFolderTreeView().getRoot());

        TreeItem treeItem = createTreeItem();

        if (treeItem != null) {
            uiState.getFolderTreeView().setRoot(treeItem);

            if (!uiState.getHorizontalSplitPane().getItems().contains(uiState.getSplitTabPane())) {
                uiState.getHorizontalSplitPane().getItems().addFirst(uiState.getSplitTabPane());
                uiState.getHorizontalSplitPane().setDividerPosition(0, 0.25);
            }

            if (FolderTreeViewState.instance.getTreeViewStructure() != null) {
                fillTreeViewWithExpansionState(FolderTreeViewState.instance.getTreeViewStructure(), uiState.getFolderTreeView().getRoot());
            }
            uiState.getFolderTreeView().getRoot().setExpanded(true);
        }
    }

    public void toggleFolderTreeView() {
        UIState uiState = UIState.instance;

        if (uiState.getHorizontalSplitPane().getItems().contains(uiState.getSplitTabPane())) {
            FolderTreeViewState.instance.setTreeViewStructure(new TreeViewStructure());
            fillExpansionState(FolderTreeViewState.instance.getTreeViewStructure(), uiState.getFolderTreeView().getRoot());
            uiState.getHorizontalSplitPane().getItems().remove(uiState.getSplitTabPane());
        } else {
            uiState.getFolderTreeView().setRoot(createTreeItem());
            uiState.getHorizontalSplitPane().getItems().addFirst(uiState.getSplitTabPane());
            uiState.getHorizontalSplitPane().setDividerPosition(0, 0.25);

            if (FolderTreeViewState.instance.getTreeViewStructure() != null) {
                fillTreeViewWithExpansionState(FolderTreeViewState.instance.getTreeViewStructure(), uiState.getFolderTreeView().getRoot());
            }
        }
    }

    public TreeItem createTreeItem() {
        File rootFile = FolderTreeViewState.instance.getOpenedFolder();

        if (rootFile != null) {
            FileTreeItem parent = new FileTreeItem();
            createTreeItem(rootFile, parent);
            return parent.getChildren().getFirst();
        }

        return null;
    }

    private void createTreeItem(File rootFile, TreeItem parent) {
        if (rootFile.isDirectory()) {
            TreeItem node = new FileTreeItem("\uD83D\uDCC1 " + rootFile.getName(), rootFile);
            parent.getChildren().add(node);
            for (File f : rootFile.listFiles()) {
                TreeItem placeholder = new FileTreeItem();
                node.getChildren().add(placeholder);

                node.addEventHandler(TreeItem.branchExpandedEvent(), new EventHandler() {
                    @Override
                    public void handle(Event event) {
                        createTreeItem(f, node);
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

    private void fillExpansionState(TreeViewStructure treeViewStructure, TreeItem treeItem) {
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

    private void fillTreeViewWithExpansionState(TreeViewStructure treeViewStructure, TreeItem treeItem) {
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
