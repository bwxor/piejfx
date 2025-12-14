package com.bwxor.piejfx.controller;

import com.bwxor.piejfx.factory.ContextMenuFactory;
import com.bwxor.piejfx.state.MaximizeState;
import com.bwxor.piejfx.state.StageState;
import com.bwxor.piejfx.state.ThemeState;
import com.bwxor.piejfx.type.RemoveSelectedTabFromPaneResponse;
import com.bwxor.piejfx.utility.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class EditorController {
    private List<String> parameters;
    @FXML
    private AnchorPane titleBarAnchorPane;
    @FXML
    private Button closeButton;
    @FXML
    private Button minimizeButton;
    @FXML
    private SplitPane splitPane;
    @FXML
    private TabPane editorTabPane;
    @FXML
    private TabPane terminalTabPane;
    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    private Menu themesMenu;

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    public void handleParameters() {
        if (!parameters.isEmpty()) {
            OpenFileUtility.openFile(splitPane, editorTabPane, terminalTabPane, new File(parameters.getFirst()));
        } else {
            EditorTabPaneUtility.addTabToPane(splitPane, editorTabPane, terminalTabPane, "Untitled");
        }

        splitPane.getItems().remove(terminalTabPane);
        TerminalTabPaneUtility.addTabToPane(terminalTabPane, "cmd.exe");
        terminalTabPane.setContextMenu(ContextMenuFactory.createTerminalTabPaneContextMenu(terminalTabPane));
    }

    @FXML
    public void initialize() {
        ThemeUtility.loadMenuWithThemes(themesMenu, ThemeState.instance.getThemes());
    }

    @FXML
    public void onCloseButtonClick() {
        int size = editorTabPane.getTabs().size();
        editorTabPane.getSelectionModel().select(size - 1);

        for (int i = 0; i < size; i++) {
            var saveResponse = EditorTabPaneUtility.removeSelectedTabFromPane(splitPane, editorTabPane, terminalTabPane);
            if (saveResponse.equals(RemoveSelectedTabFromPaneResponse.CANCELLED)) {
                return;
            }
        }

        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
        Platform.exit();
    }

    @FXML
    public void onMinimizeButtonClick() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    public void handleDoubleClickAction(MouseEvent mouseEvent) {
        if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
            if(mouseEvent.getClickCount() == 2){
                MaximizeState.instance.toggleMaximize(StageState.instance.getStage());
            }
        }
    }

    @FXML
    public void handleClickAction(MouseEvent mouseEvent) {
        if (!MaximizeState.instance.isMaximized()) {
            xOffset = mouseEvent.getSceneX();
            yOffset = mouseEvent.getSceneY();
        }
    }

    @FXML
    public void handleMovementAction(MouseEvent mouseEvent) {
        if (!MaximizeState.instance.isMaximized()) {
            Stage stage = (Stage) titleBarAnchorPane.getScene().getWindow();
            stage.setX(mouseEvent.getScreenX() - xOffset);
            stage.setY(mouseEvent.getScreenY() - yOffset);
        }
    }

    @FXML
    public void onNewButtonClickEvent() {
        EditorTabPaneUtility.addTabToPane(splitPane, editorTabPane, terminalTabPane, "Untitled");
    }

    @FXML
    public void onOpenButtonClickEvent() {
        OpenFileUtility.openFile(splitPane, editorTabPane, terminalTabPane);
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
                EditorTabPaneUtility.addTabToPane(splitPane, editorTabPane, terminalTabPane, "Untitled");
            } else if (keyEvent.getCode().equals(KeyCode.W)) {
                EditorTabPaneUtility.removeSelectedTabFromPane(splitPane, editorTabPane, terminalTabPane);
            } else if (keyEvent.getCode().equals(KeyCode.S)) {
                SaveFileUtility.saveFile(editorTabPane);
            } else if (keyEvent.getCode().equals(KeyCode.B)) {
                TerminalTabPaneUtility.toggleTerminalTabPane(splitPane, terminalTabPane);
            }
        }
    }
}
