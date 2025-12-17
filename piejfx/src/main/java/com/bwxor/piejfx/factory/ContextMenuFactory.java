package com.bwxor.piejfx.factory;

import com.bwxor.piejfx.control.FileTreeItem;
import com.bwxor.piejfx.dto.NewFileResponse;
import com.bwxor.piejfx.state.FolderTreeViewState;
import com.bwxor.piejfx.type.CreationType;
import com.bwxor.piejfx.type.NewFileOption;
import com.bwxor.piejfx.utility.FileOperationsUtility;
import com.bwxor.piejfx.utility.NotificationUtility;
import com.bwxor.piejfx.utility.TerminalTabPaneUtility;
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

        });
        contextMenu.getItems().add(deleteFileMenuItem);

        return contextMenu;
    }

    public static ContextMenu createCodeAreaContextMenu(SplitPane verticalSplitPane, TabPane terminalTabPane) {
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.setStyle("-fx-font-family: Segoe UI");

        MenuItem showTerminalContextMenuItem = new MenuItem();
        showTerminalContextMenuItem.setText("Toggle Terminal Tab");
        showTerminalContextMenuItem.setOnAction(e -> {
            TerminalTabPaneUtility.toggleTerminalTabPane(verticalSplitPane, terminalTabPane);
        });

        contextMenu.getItems().add(showTerminalContextMenuItem);

        return contextMenu;
    }

    public static ContextMenu createTerminalTabPaneContextMenu(TabPane tabPane) {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem newTerminalTabMenuItem = new MenuItem();
        newTerminalTabMenuItem.setText("New Terminal Tab");
        newTerminalTabMenuItem.setOnAction(e -> {
            TerminalTabPaneUtility.addTabToPane(tabPane, null);
        });
        contextMenu.getItems().add(newTerminalTabMenuItem);

        MenuItem closeTerminalTabMenuItem = new MenuItem();
        closeTerminalTabMenuItem.setText("Close Terminal Tab");
        closeTerminalTabMenuItem.setOnAction(e -> {
            TerminalTabPaneUtility.removeSelectedTabFromPane(tabPane);
        });
        contextMenu.getItems().add(closeTerminalTabMenuItem);

        return contextMenu;
    }

    private static void createFile(CreationType creationType, TreeView folderTreeView) {
        String windowTitle;
        String itemViewPrefix;

        if (creationType.equals(CreationType.FILE)) {
            windowTitle = "New File";
            itemViewPrefix = "";
        } else {
            windowTitle = "New Folder";
            itemViewPrefix = "\uD83D\uDCC1";
        }

        NewFileResponse response = FileOperationsUtility.showNewFileWindow(windowTitle);

        try {
            File fileToCreate;

            if (response != null && response.option().equals(NewFileOption.CREATE)) {

                if (folderTreeView.getSelectionModel().getSelectedIndex() > 0) {
                    File file = ((FileTreeItem) folderTreeView.getSelectionModel().getSelectedItem()).getFile();

                    fileToCreate = new File(Paths.get(file.isDirectory() ? file.toString() : file.getParentFile().toString(), response.fileName()).toUri());

                    if (!fileToCreate.exists()) {
                        if (creationType.equals(CreationType.FILE)) {
                            fileToCreate.createNewFile();
                        } else {
                            fileToCreate.mkdir();
                        }

                        FileTreeItem fileTreeItem = new FileTreeItem(itemViewPrefix + " " + fileToCreate.getName(), fileToCreate);
                        if (file.isDirectory()) {
                            ((FileTreeItem) folderTreeView.getSelectionModel().getSelectedItem()).getChildren().add(fileTreeItem);
                        } else {
                            ((FileTreeItem) folderTreeView.getSelectionModel().getSelectedItem()).getParent().getChildren().add(fileTreeItem);
                        }
                    }
                    else {
                        NotificationUtility.showNotificationOk("File already exists.");
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
                        NotificationUtility.showNotificationOk("File already exists.");
                    }
                }
            }
        } catch (IOException ex) {
            NotificationUtility.showNotificationOk("Error while trying to create the file.");
        }
    }
}
