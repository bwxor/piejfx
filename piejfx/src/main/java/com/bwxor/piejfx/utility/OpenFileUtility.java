package com.bwxor.piejfx.utility;

import com.bwxor.piejfx.state.CodeAreaState;
import com.bwxor.piejfx.state.StageState;
import com.bwxor.piejfx.state.UIState;
import javafx.stage.FileChooser;

import java.io.File;

public class OpenFileUtility {
    public static void openFile() {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(StageState.instance.getStage());

        if (selectedFile != null) {
            openFile(selectedFile);
        }
    }

    public static void openFile(File file) {
        UIState uiState = UIState.getInstance();

        EditorTabPaneUtility.addTabToPane(file);

        CodeAreaState.IndividualState state = CodeAreaState.instance.getIndividualStates().get(uiState.getEditorTabPane().getSelectionModel().getSelectedIndex());

        state.setSaved(true);
        uiState.getEditorTabPane().getSelectionModel().getSelectedItem().setText(state.getOpenedFile().getName());
    }
}
