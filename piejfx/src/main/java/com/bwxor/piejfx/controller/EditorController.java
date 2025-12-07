package com.bwxor.piejfx.controller;

import com.bwxor.piejfx.state.CodeAreaState;
import com.bwxor.piejfx.utility.OpenFileUtility;
import com.bwxor.piejfx.utility.SaveFileUtility;
import com.bwxor.piejfx.utility.TabPaneUtility;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class EditorController {
    @FXML
    private TabPane tabPane;

    @FXML
    public void initialize() {
        TabPaneUtility.addTabToPane(tabPane, "Untitled");
    }

    @FXML
    public void onNewButtonClickEvent() {
        TabPaneUtility.addTabToPane(tabPane, "Untitled");
    }

    @FXML
    public void onOpenButtonClickEvent() {
        OpenFileUtility.openFile(tabPane);
    }

    @FXML
    public void onSaveButtonClickEvent() {
        SaveFileUtility.saveFile(tabPane);
    }

    @FXML
    public void onSaveAsButtonClickEvent() {
        SaveFileUtility.saveFileAs(tabPane);
    }

    @FXML
    public void onKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.isControlDown()) { // Modifiers will be handled
            if (keyEvent.getCode().equals(KeyCode.T)) {
                TabPaneUtility.addTabToPane(tabPane, "Untitled");
            }
            else if (keyEvent.getCode().equals(KeyCode.W)) {
                TabPaneUtility.removeSelectedTabFromPane(tabPane);
            }
        }
    }
}
