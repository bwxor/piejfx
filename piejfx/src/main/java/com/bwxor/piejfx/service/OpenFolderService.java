package com.bwxor.piejfx.service;

import com.bwxor.piejfx.state.FolderTreeViewState;
import com.bwxor.piejfx.state.ServiceState;
import com.bwxor.piejfx.state.StageState;
import javafx.stage.DirectoryChooser;

import java.io.File;

public class OpenFolderService {
    public void openFolder() {
        ServiceState serviceState = ServiceState.getInstance();
        
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedFile = directoryChooser.showDialog(StageState.instance.getStage());

        if (selectedFile != null) {
            FolderTreeViewState.instance.setOpenedFolder(selectedFile);
            serviceState.getFolderTreeViewService().showFolderTreeView();
        }
    }
}
