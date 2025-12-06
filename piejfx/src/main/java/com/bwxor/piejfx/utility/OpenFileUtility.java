package com.bwxor.piejfx.utility;

import com.bwxor.piejfx.state.StageState;
import javafx.scene.control.TabPane;
import javafx.stage.FileChooser;

import java.io.File;

public class OpenFileUtility {
    public static void openFile(TabPane tabPane) {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(StageState.instance.getStage());

        if (selectedFile != null) {
            TabPaneUtility.addTabToPane(tabPane, selectedFile);
        }
    }
}
