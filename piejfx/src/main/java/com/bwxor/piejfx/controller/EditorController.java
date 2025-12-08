package com.bwxor.piejfx.controller;

import com.bwxor.piejfx.state.ThemeState;
import com.bwxor.piejfx.utility.*;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.File;
import java.util.List;

public class EditorController {
    private List<String> parameters;
    @FXML
    private TabPane tabPane;

    @FXML
    private Menu themesMenu;

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    public void handleParameters() {
        if (!parameters.isEmpty()) {
            OpenFileUtility.openFile(tabPane, new File(parameters.getFirst()));
        }
        else {
            TabPaneUtility.addTabToPane(tabPane, "Untitled");
        }
    }

    @FXML
    public void initialize() {
        ThemeUtility.loadMenuWithThemes(themesMenu, ThemeState.instance.getThemes());
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
    public void onAboutButtonClickEvent() {
        AboutUtility.showAboutPage();
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
            else if (keyEvent.getCode().equals(KeyCode.S)) {
                SaveFileUtility.saveFile(tabPane);
            }
        }
    }
}
