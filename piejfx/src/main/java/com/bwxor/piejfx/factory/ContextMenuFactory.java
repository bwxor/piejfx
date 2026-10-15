package com.bwxor.piejfx.factory;

import com.bwxor.piejfx.control.FileTreeItem;
import com.bwxor.piejfx.dto.TreeViewStructure;
import com.bwxor.piejfx.mapper.FileToGraphicMapper;
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
        ServiceState serviceState = ServiceState.instance;
        ContextMenu contextMenu = new ContextMenu();

        MenuItem newFileMenuItem = new MenuItem("New File");
        newFileMenuItem.setOnAction(e -> serviceState.getFolderTreeViewService().createFile(CreationType.FILE, folderTreeView));
        contextMenu.getItems().add(newFileMenuItem);

        MenuItem newFolderMenuItem = new MenuItem("New Folder");
        newFolderMenuItem.setOnAction(e -> serviceState.getFolderTreeViewService().createFile(CreationType.FOLDER, folderTreeView));
        contextMenu.getItems().add(newFolderMenuItem);

        MenuItem renameMenuItem = new MenuItem("Rename");
        renameMenuItem.setOnAction(e -> serviceState.getFolderTreeViewService().renameFile(folderTreeView));
        contextMenu.getItems().add(renameMenuItem);

        MenuItem deleteFileMenuItem = new MenuItem("Delete");
        deleteFileMenuItem.setOnAction(e ->
        {
            serviceState.getFolderTreeViewService().deleteFile(folderTreeView);
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
        newTerminalTabMenuItem.setText("New Terminal");
        newTerminalTabMenuItem.setOnAction(e -> {
            serviceState.getTerminalTabPaneService().addTabToPane(null);
        });
        contextMenu.getItems().add(newTerminalTabMenuItem);

        MenuItem closeTerminalTabMenuItem = new MenuItem();
        closeTerminalTabMenuItem.setText("Close Current Terminal");
        closeTerminalTabMenuItem.setOnAction(_ -> serviceState.getTerminalTabPaneService().removeSelectedTabFromPane(uiState.getTerminalTabPane()));
        contextMenu.getItems().add(closeTerminalTabMenuItem);

        MenuItem closeAllTerminalsTab = new MenuItem();
        closeAllTerminalsTab.setText("Close All Terminals");
        closeAllTerminalsTab.setOnAction(e -> {
            serviceState.getTerminalTabPaneService().removeAllTabsFromPane(uiState.getTerminalTabPane());
        });
        contextMenu.getItems().add(closeAllTerminalsTab);

        return contextMenu;
    }

    public static ContextMenu createEditorTabPaneContextMenu() {
        ServiceState serviceState = ServiceState.instance;

        ContextMenu contextMenu = new ContextMenu();

        MenuItem closeMenuItem = new MenuItem();
        closeMenuItem.setText("Close");
        closeMenuItem.setOnAction(_ -> serviceState.getEditorTabPaneService().removeSelectedTabFromPane());
        contextMenu.getItems().add(closeMenuItem);

        MenuItem closeOtherTabsMenuItem = new MenuItem();
        closeOtherTabsMenuItem.setText("Close Other Tabs");
        closeOtherTabsMenuItem.setOnAction(_ -> {
            serviceState.getEditorTabPaneService().removeOtherTabsFromPane();
        });
        contextMenu.getItems().add(closeOtherTabsMenuItem);

        MenuItem closeAllTabsMenuItem = new MenuItem();
        closeAllTabsMenuItem.setText("Close All Tabs");
        closeAllTabsMenuItem.setOnAction(e -> {
            serviceState.getEditorTabPaneService().removeAllTabsFromPane();
        });
        contextMenu.getItems().add(closeAllTabsMenuItem);

        return contextMenu;
    }


}