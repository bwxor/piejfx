package com.bwxor.piejfx.utility;

import com.bwxor.piejfx.state.CodeAreaState;
import com.bwxor.piejfx.state.StageState;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.stage.FileChooser;

import java.io.File;

public class OpenFileUtility {
    public static void openFile(SplitPane splitPane, TabPane editorTabPane, TabPane terminalTabPane) {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(StageState.instance.getStage());

        if (selectedFile != null) {
            openFile(splitPane, editorTabPane, terminalTabPane, selectedFile);
        }
    }

    public static void openFile(SplitPane splitPane, TabPane editorTabPane, TabPane terminalTabPane, File file) {
        EditorTabPaneUtility.addTabToPane(splitPane, editorTabPane, terminalTabPane, file);

        CodeAreaState.IndividualState state = CodeAreaState.instance.getIndividualStates().get(editorTabPane.getSelectionModel().getSelectedIndex());

        state.setSaved(true);
        editorTabPane.getSelectionModel().getSelectedItem().setText(state.getOpenedFile().getName());
    }
}
