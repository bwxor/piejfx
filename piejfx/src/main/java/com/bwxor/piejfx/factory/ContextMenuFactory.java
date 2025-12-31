package com.bwxor.piejfx.factory;

import com.bwxor.piejfx.control.FileTreeItem;
import com.bwxor.piejfx.state.FolderTreeViewState;
import com.bwxor.piejfx.state.ServiceState;
import com.bwxor.piejfx.state.UIState;
import com.bwxor.piejfx.type.CreationType;
import com.bwxor.plugin.dto.NewFileResponse;
import com.bwxor.plugin.type.NewFileOption;
import com.bwxor.plugin.type.NotificationYesNoCancelOption;
import javafx.scene.control.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class ContextMenuFactory {
    public static ContextMenu createFolderTreeViewContextMenu(TreeView folderTreeView) {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem newFileMenuItem = new MenuItem("New File");
        newFileMenuItem.setOnAction(e -> createFile(CreationType.FILE, folderTreeView));
        contextMenu.getItems().add(newFileMenuItem);

        MenuItem newFolderMenuItem = new MenuItem("New Folder");
        newFolderMenuItem.setOnAction(e -> createFile(CreationType.FOLDER, folderTreeView));
        contextMenu.getItems().add(newFolderMenuItem);

        MenuItem deleteFileMenuItem = new MenuItem("Delete");
        deleteFileMenuItem.setOnAction(e ->
        {
            deleteFile(folderTreeView);
        });
        contextMenu.getItems().add(deleteFileMenuItem);

        return contextMenu;
    }

    public static ContextMenu createCodeAreaContextMenu() {
        ServiceState serviceState = ServiceState.getInstance();
        
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.setStyle("-fx-font-family: Segoe UI");

        MenuItem showTerminalContextMenuItem = new MenuItem();
        showTerminalContextMenuItem.setText("Toggle Terminal Tab");
        showTerminalContextMenuItem.setOnAction(e -> {
            serviceState.getTerminalTabPaneService().toggleTerminalTabPane();
        });

        contextMenu.getItems().add(showTerminalContextMenuItem);

        return contextMenu;
    }

    public static ContextMenu createTerminalTabPaneContextMenu() {
        UIState uiState = UIState.getInstance();
        ServiceState serviceState = ServiceState.getInstance();

        ContextMenu contextMenu = new ContextMenu();

        MenuItem newTerminalTabMenuItem = new MenuItem();
        newTerminalTabMenuItem.setText("New Terminal Tab");
        newTerminalTabMenuItem.setOnAction(e -> {
            serviceState.getTerminalTabPaneService().addTabToPane(null);
        });
        contextMenu.getItems().add(newTerminalTabMenuItem);

        MenuItem closeTerminalTabMenuItem = new MenuItem();
        closeTerminalTabMenuItem.setText("Close Terminal Tab");
        closeTerminalTabMenuItem.setOnAction(e -> {
            serviceState.getTerminalTabPaneService().removeSelectedTabFromPane(uiState.getTerminalTabPane());
        });
        contextMenu.getItems().add(closeTerminalTabMenuItem);

        return contextMenu;
    }

    private static void createFile(CreationType creationType, TreeView folderTreeView) {
        ServiceState serviceState = ServiceState.getInstance();

        String windowTitle;
        String itemViewPrefix;

        if (creationType.equals(CreationType.FILE)) {
            windowTitle = "New File";
            itemViewPrefix = "";
        } else {
            windowTitle = "New Folder";
            itemViewPrefix = "\uD83D\uDCC1";
        }

        NewFileResponse response = serviceState.getFileService().showNewFileWindow(windowTitle);

        try {
            File fileToCreate;

            if (response != null && response.option().equals(NewFileOption.CREATE)) {

                if (folderTreeView.getSelectionModel().getSelectedIndex() > 0) {
                    File file = ((FileTreeItem) folderTreeView.getSelectionModel().getSelectedItem()).getFile();

                    fileToCreate = new File(Paths.get(file.isDirectory() ? file.toString() : file.getParentFile().toString(), response.fileName()).toUri());

                    if (!fileToCreate.exists()) {
                        if (creationType.equals(CreationType.FILE)) {
                            fileToCreate.createNewFile();
                            serviceState.getPluginService().invokeOnCreateFile(fileToCreate);
                        } else {
                            fileToCreate.mkdir();
                            serviceState.getPluginService().invokeOnCreateFolder(fileToCreate);
                        }

                        FileTreeItem fileTreeItem = new FileTreeItem(itemViewPrefix + " " + fileToCreate.getName(), fileToCreate);
                        if (file.isDirectory()) {
                            ((FileTreeItem) folderTreeView.getSelectionModel().getSelectedItem()).getChildren().add(fileTreeItem);
                        } else {
                            ((FileTreeItem) folderTreeView.getSelectionModel().getSelectedItem()).getParent().getChildren().add(fileTreeItem);
                        }
                    }
                    else {
                        serviceState.getNotificationService().showNotificationOk("File already exists.");
                    }
                } else {
                    fileToCreate = new File(Paths.get(FolderTreeViewState.instance.getOpenedFolder().toString(), response.fileName()).toUri());

                    if (!fileToCreate.exists()) {

                        if (creationType.equals(CreationType.FILE)) {
                            fileToCreate.createNewFile();
                        } else {
                            fileToCreate.mkdir();
                        }

                        ((FileTreeItem) folderTreeView.getRoot()).getChildren().add(new FileTreeItem(itemViewPrefix + " " + fileToCreate.getName(), fileToCreate));
                    } else {
                        serviceState.getNotificationService().showNotificationOk("File already exists.");
                    }
                }
            }
        } catch (IOException ex) {
            serviceState.getNotificationService().showNotificationOk("Error while trying to create the file.");
        }
    }

    private static void deleteFile(TreeView folderTreeView) {
        ServiceState serviceState = ServiceState.getInstance();

        if (folderTreeView.getSelectionModel().getSelectedIndex() > 0) {
            if (folderTreeView.getSelectionModel().getSelectedItem().equals(folderTreeView.getRoot())) {
                return;
            }

            NotificationYesNoCancelOption response = serviceState.getNotificationService().showNotificationYesNoCancel("Are you sure you want to delete the file?");

            if (response.equals(NotificationYesNoCancelOption.YES)) {
                FileTreeItem treeItem = (FileTreeItem) folderTreeView.getSelectionModel().getSelectedItem();

                if (treeItem.getFile().isDirectory()) {
                    if (!serviceState.getFileService().deleteFolder(treeItem.getFile())) {
                        serviceState.getNotificationService().showNotificationOk("Could not delete file.");
                    }
                    else {
                        treeItem.getParent().getChildren().remove(treeItem);
                    }
                }
                else {
                    boolean deletionStatus = treeItem.getFile().delete();

                    if (!deletionStatus) {
                        serviceState.getNotificationService().showNotificationOk("Could not delete file.");
                    } else {
                        treeItem.getParent().getChildren().remove(treeItem);
                        serviceState.getPluginService().invokeOnDeleteFile(treeItem.getFile());
                    }
                }
            }
        }
    }
}
