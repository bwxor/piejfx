package com.bwxor.piejfx.utility;

import com.bwxor.piejfx.state.FolderTreeViewState;
import com.bwxor.piejfx.state.StageState;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeView;
import javafx.stage.DirectoryChooser;

import java.io.File;

public class OpenFolderUtility {
    public static void openFolder() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedFile = directoryChooser.showDialog(StageState.instance.getStage());

        if (selectedFile != null) {
            FolderTreeViewState.instance.setOpenedFolder(selectedFile);
            FolderTreeViewUtility.showFolderTreeView();
        }
    }
}
