package com.bwxor.piejfx.utility;

import com.bwxor.piejfx.state.CodeAreaState;
import com.bwxor.piejfx.state.StageState;
import javafx.scene.control.TabPane;
import javafx.stage.FileChooser;

import java.io.File;

public class OpenFileUtility {
    public static void openFile(TabPane tabPane) {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(StageState.instance.getStage());

        if (selectedFile != null) {
            openFile(tabPane, selectedFile);
        }
    }

    public static void openFile(TabPane tabPane, File file) {
        EditorTabPaneUtility.addTabToPane(tabPane, file);

        CodeAreaState.IndividualState state = CodeAreaState.instance.getIndividualStates().get(tabPane.getSelectionModel().getSelectedIndex());

        state.setSaved(true);
        tabPane.getSelectionModel().getSelectedItem().setText(state.getOpenedFile().getName());
    }
}
