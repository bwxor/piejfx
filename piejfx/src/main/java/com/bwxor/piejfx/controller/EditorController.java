package com.bwxor.piejfx.controller;

import com.bwxor.piejfx.state.ThemeState;
import com.bwxor.piejfx.utility.*;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.File;
import java.util.List;

public class EditorController {
    private List<String> parameters;
    @FXML
    private SplitPane splitPane;
    @FXML
    private TabPane editorTabPane;
    @FXML
    private TabPane terminalTabPane;

    @FXML
    private Menu themesMenu;

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    public void handleParameters() {
        if (!parameters.isEmpty()) {
            OpenFileUtility.openFile(editorTabPane, new File(parameters.getFirst()));
        }
        else {
            EditorTabPaneUtility.addTabToPane(editorTabPane, "Untitled");
        }

        splitPane.getItems().remove(terminalTabPane);
        TerminalTabPaneUtility.addTabToPane(terminalTabPane, "cmd.exe");
    }

    @FXML
    public void initialize() {
        ThemeUtility.loadMenuWithThemes(themesMenu, ThemeState.instance.getThemes());
    }

    @FXML
    public void onNewButtonClickEvent() {
        EditorTabPaneUtility.addTabToPane(editorTabPane, "Untitled");
    }

    @FXML
    public void onOpenButtonClickEvent() {
        OpenFileUtility.openFile(editorTabPane);
    }

    @FXML
    public void onSaveButtonClickEvent() {
        SaveFileUtility.saveFile(editorTabPane);
    }

    @FXML
    public void onSaveAsButtonClickEvent() {
        SaveFileUtility.saveFileAs(editorTabPane);
    }

    @FXML
    public void onAboutButtonClickEvent() {
        AboutUtility.showAboutPage();
    }

    @FXML
    public void onKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.isControlDown()) { // Modifiers will be handled
            if (keyEvent.getCode().equals(KeyCode.T)) {
                EditorTabPaneUtility.addTabToPane(editorTabPane, "Untitled");
            }
            else if (keyEvent.getCode().equals(KeyCode.W)) {
                EditorTabPaneUtility.removeSelectedTabFromPane(editorTabPane);
            }
            else if (keyEvent.getCode().equals(KeyCode.S)) {
                SaveFileUtility.saveFile(editorTabPane);
            }
            else if (keyEvent.getCode().equals(KeyCode.B)) {
                if (splitPane.getItems().contains(terminalTabPane)) {
                    splitPane.getItems().remove(terminalTabPane);
                }
                else {
                    splitPane.getItems().add(terminalTabPane);
                }
            }
        }
    }
}
