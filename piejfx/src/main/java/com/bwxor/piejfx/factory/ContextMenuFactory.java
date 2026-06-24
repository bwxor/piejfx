package com.bwxor.piejfx.factory;

import com.bwxor.piejfx.control.FileTreeItem;
import com.bwxor.piejfx.dto.TreeViewStructure;
import com.bwxor.piejfx.state.CodeAreaState;
import com.bwxor.piejfx.state.FolderTreeViewState;
import com.bwxor.piejfx.state.ServiceState;
import com.bwxor.piejfx.state.UIState;
import com.bwxor.piejfx.type.CreationType;
import com.bwxor.plugin.dto.NewFileResponse;
import com.bwxor.plugin.dto.RenameFileResponse;
import com.bwxor.plugin.type.NewFileOption;
import com.bwxor.plugin.type.NotificationYesNoCancelOption;
import com.bwxor.plugin.type.RenameFileOption;
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

        MenuItem renameMenuItem = new MenuItem("Rename");
        renameMenuItem.setOnAction(e -> renameFile(folderTreeView));
        contextMenu.getItems().add(renameMenuItem);

        MenuItem deleteFileMenuItem = new MenuItem("Delete");
        deleteFileMenuItem.setOnAction(e ->
        {
            deleteFile(folderTreeView);
        });
        contextMenu.getItems().add(deleteFileMenuItem);

        return contextMenu;
    }

    public static ContextMenu createCodeAreaContextMenu() {
        ServiceState serviceState = ServiceState.instance;

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
        UIState uiState = UIState.instance;
        ServiceState serviceState = ServiceState.instance;

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
        ServiceState serviceState = ServiceState.instance;

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
                    } else {
                        serviceState.getNotificationService().showNotificationOk("File already exists.");
                    }
                } else {
                    fileToCreate = new File(Paths.get(FolderTreeViewState.instance.getOpenedFolder().toString(), response.fileName()).toUri());

                    if (!fileToCreate.exists()) {

                        if (creationType.equals(CreationType.FILE)) {
                            fileToCreate.createNewFile();
                            serviceState.getPluginService().invokeOnCreateFile(fileToCreate);
                        } else {
                            fileToCreate.mkdir();
                            serviceState.getPluginService().invokeOnCreateFolder(fileToCreate);
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

    private static void renameFile(TreeView folderTreeView) {
        ServiceState serviceState = ServiceState.instance;
        CodeAreaState codeAreaState = CodeAreaState.instance;
        UIState uiState = UIState.instance;

        String itemViewPrefix;

        File file = ((FileTreeItem) folderTreeView.getSelectionModel().getSelectedItem()).getFile();

        if (file.isDirectory()) {
            itemViewPrefix = "\uD83D\uDCC1";
        } else {
            itemViewPrefix = "";
        }

        RenameFileResponse response = serviceState.getFileService().showRenameFileWindow(file.getName());

        File fileWithPotentialNewName;

        if (response != null && response.option().equals(RenameFileOption.RENAME)) {
            if (folderTreeView.getSelectionModel().getSelectedIndex() > 0) {
                fileWithPotentialNewName = new File(Paths.get(file.getParentFile().toString(), response.newName()).toUri());

                if (!fileWithPotentialNewName.exists()) {
                    var treeItemToRename = (FileTreeItem) folderTreeView.getSelectionModel().getSelectedItem();
                    treeItemToRename.setValue(itemViewPrefix + " " + fileWithPotentialNewName.getName());
                    treeItemToRename.setFile(fileWithPotentialNewName);

                    // If a file, update it in the TabPane and title bar view
                    if (!file.isDirectory()) {
                        for (int i = 0; i < codeAreaState.getIndividualStates().size(); i++) {
                            var state = codeAreaState.getIndividualStates().get(i);

                            if (state.getOpenedFile() != null && state.getOpenedFile().equals(file)) {
                                state.setOpenedFile(fileWithPotentialNewName);
                                uiState.getEditorTabPane().getTabs().get(i).setText(fileWithPotentialNewName.getName());
                                if (uiState.getEditorTabPane().getSelectionModel().getSelectedIndex() == i) {
                                    uiState.getTitleBarLabel().setText(fileWithPotentialNewName.getName());
                                }
                            }
                        }
                    } else {
                        // If directory, recursively update all child TreeItems
                        updateTreeItemPathsRecursively(treeItemToRename, file.getAbsolutePath(), fileWithPotentialNewName.getAbsolutePath());

                        // Update TreeViewStructure to maintain expansion state
                        FolderTreeViewState folderTreeViewState = FolderTreeViewState.instance;
                        if (folderTreeViewState.getTreeViewStructure() != null) {
                            updateTreeViewStructurePathsRecursively(folderTreeViewState.getTreeViewStructure(),
                                    file.getAbsolutePath(), fileWithPotentialNewName.getAbsolutePath());
                        }

                        String oldDirPath = file.getAbsolutePath();
                        String newDirPath = fileWithPotentialNewName.getAbsolutePath();

                        for (int i = 0; i < codeAreaState.getIndividualStates().size(); i++) {
                            var state = codeAreaState.getIndividualStates().get(i);

                            if (state.getOpenedFile() != null) {
                                String openedFilePath = state.getOpenedFile().getAbsolutePath();

                                if (openedFilePath.startsWith(oldDirPath + File.separator) || openedFilePath.equals(oldDirPath)) {
                                    String relativePath = openedFilePath.substring(oldDirPath.length());
                                    File newFile = new File(newDirPath + relativePath);

                                    state.setOpenedFile(newFile);

                                    uiState.getEditorTabPane().getTabs().get(i).setText(newFile.getName());

                                    if (uiState.getEditorTabPane().getSelectionModel().getSelectedIndex() == i) {
                                        uiState.getTitleBarLabel().setText(newFile.getName());
                                    }
                                }
                            }
                        }
                    }

                    file.renameTo(fileWithPotentialNewName);

                    serviceState.getPluginService().invokeOnRenameFile(file);
                } else {
                    serviceState.getNotificationService().showNotificationOk("File already exists.");
                }
            } else {
                serviceState.getNotificationService().showNotificationOk("You can only rename non-root files.");
            }
        }
    }

    /**
     * Recursively updates the file paths of all child TreeItems when a directory is renamed.
     * For unexpanded directories, clears children and collapses them so they'll be properly
     * reloaded with the new path when expanded again.
     *
     * @param treeItem The TreeItem representing the renamed directory
     * @param oldPath  The old absolute path of the directory
     * @param newPath  The new absolute path of the directory
     */
    private static void updateTreeItemPathsRecursively(FileTreeItem treeItem, String oldPath, String newPath) {
        for (var child : treeItem.getChildren()) {
            if (child instanceof FileTreeItem) {
                FileTreeItem fileTreeItem = (FileTreeItem) child;
                File oldFile = fileTreeItem.getFile();

                if (oldFile != null) {
                    String oldFilePath = oldFile.getAbsolutePath();

                    String relativePath = oldFilePath.substring(oldPath.length());
                    File newFile = new File(newPath + relativePath);

                    fileTreeItem.setFile(newFile);

                    if (oldFile.isDirectory()) {
                        if (fileTreeItem.isExpanded() && fileTreeItem.getChildren().size() > 0) {
                            // Directory is expanded, recursively update all children
                            updateTreeItemPathsRecursively(fileTreeItem, oldFilePath, newFile.getAbsolutePath());
                        } else if (fileTreeItem.getChildren().size() > 0) {
                            // Directory is not expanded but has placeholder children
                            // Clear them and collapse so they'll be reloaded with correct paths on next expansion
                            fileTreeItem.getChildren().clear();
                            fileTreeItem.setExpanded(false);
                            
                            // Add new placeholders based on the new directory path
                            File[] files = newFile.listFiles();
                            if (files != null && files.length > 0) {
                                // Add a single placeholder to indicate the folder has children
                                fileTreeItem.getChildren().add(new FileTreeItem());
                            }
                        }
                    }
                }
            }
        }
    }

    private static void deleteFile(TreeView folderTreeView) {
        ServiceState serviceState = ServiceState.instance;

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
                    } else {
                        treeItem.getParent().getChildren().remove(treeItem);
                        serviceState.getPluginService().invokeOnDeleteFile(treeItem.getFile());
                    }
                } else {
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

    /**
     * Recursively updates the file paths in TreeViewStructure when a directory is renamed.
     * This ensures that the expansion state is preserved correctly when the tree is refreshed.
     *
     * @param treeViewStructure The TreeViewStructure to update
     * @param oldPath           The old absolute path of the directory
     * @param newPath           The new absolute path of the directory
     */
    private static void updateTreeViewStructurePathsRecursively(TreeViewStructure treeViewStructure, String oldPath, String newPath) {
        if (treeViewStructure == null) {
            return;
        }

        // Update the file reference if it matches the old path or is within it
        if (treeViewStructure.getFile() != null) {
            String filePath = treeViewStructure.getFile().getAbsolutePath();

            if (filePath.equals(oldPath) || filePath.startsWith(oldPath + File.separator)) {
                String relativePath = filePath.substring(oldPath.length());
                File newFile = new File(newPath + relativePath);
                treeViewStructure.setFile(newFile);
            }
        }

        // Recursively update all children
        if (treeViewStructure.getChildren() != null) {
            for (TreeViewStructure child : treeViewStructure.getChildren()) {
                updateTreeViewStructurePathsRecursively(child, oldPath, newPath);
            }
        }
    }
}