package com.bwxor.piejfx.controller;

import com.bwxor.piejfx.factory.TabFactory;
import com.bwxor.piejfx.utility.TabPaneUtility;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class EditorController {
    @FXML
    private TabPane tabPane;

    @FXML
    public void initialize() {
        TabPaneUtility.addTabToPane(tabPane, "New file");
    }

    @FXML
    public void onKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.isControlDown()) { // Modifiers will be handled
            if (keyEvent.getCode().equals(KeyCode.T)) {
                TabPaneUtility.addTabToPane(tabPane, "New file");
            }
            else if (keyEvent.getCode().equals(KeyCode.W)) {
                TabPaneUtility.removeSelectedTabFromPane(tabPane);
            }
        }
    }
}
