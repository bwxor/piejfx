package com.bwxor.piejfx.service;

import com.bwxor.piejfx.state.CodeAreaState;
import com.bwxor.piejfx.state.ServiceState;
import com.bwxor.piejfx.state.StageState;
import com.bwxor.piejfx.state.UIState;
import javafx.stage.FileChooser;

import java.io.File;

public class OpenFileService {
    public void openFile() {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(StageState.instance.getStage());

        if (selectedFile != null) {
            openFile(selectedFile);
        }
    }

    public void openFile(File file) {
        ServiceState serviceState = ServiceState.getInstance();
        
        UIState uiState = UIState.getInstance();

        serviceState.getEditorTabPaneService().addTabToPane(file);

        CodeAreaState.IndividualState state = CodeAreaState.instance.getIndividualStates().get(uiState.getEditorTabPane().getSelectionModel().getSelectedIndex());

        state.setSaved(true);
        uiState.getEditorTabPane().getSelectionModel().getSelectedItem().setText(state.getOpenedFile().getName());
    }
}
