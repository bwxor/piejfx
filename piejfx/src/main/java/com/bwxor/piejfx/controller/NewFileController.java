package com.bwxor.piejfx.controller;

import com.bwxor.piejfx.dto.NewFileResponse;
import com.bwxor.piejfx.type.NewFileOption;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class NewFileController {
    @FXML
    private AnchorPane titleBarAnchorPane;
    @FXML
    private Button closeButton;
    @FXML
    private Button minimizeButton;
    @FXML
    private Label windowTitle;
    @FXML
    private TextField fileNameTextArea;
    @FXML
    private Button buttonCreate;
    @FXML
    private Button buttonCancel;
    private NewFileResponse newFileResponse;
    private double xOffset = 0;
    private double yOffset = 0;

    public void setWindowTitle(String windowTitle) {
        this.windowTitle.setText(windowTitle);
    }

    public void initialize() {
        buttonCreate.setDisable(true);
        Platform.runLater(() -> fileNameTextArea.requestFocus());
    }

    public void setNewFileResponse(NewFileResponse newFileResponse) {
        this.newFileResponse = newFileResponse;
    }

    public NewFileResponse getNewFileResponse() {
        return newFileResponse;
    }

    @FXML
    public void onCloseButtonClick() {
        newFileResponse = new NewFileResponse(NewFileOption.CANCEL, null);
        ((Stage) fileNameTextArea.getScene().getWindow()).close();
    }

    @FXML
    public void onMinimizeButtonClick() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    public void handleClickAction(MouseEvent mouseEvent) {
        xOffset = mouseEvent.getSceneX();
        yOffset = mouseEvent.getSceneY();
    }

    @FXML
    public void handleMovementAction(MouseEvent mouseEvent) {
        Stage stage = (Stage) titleBarAnchorPane.getScene().getWindow();
        stage.setX(mouseEvent.getScreenX() - xOffset);
        stage.setY(mouseEvent.getScreenY() - yOffset);
    }

    @FXML
    public void onCreateButtonClickEvent() {
        newFileResponse = new NewFileResponse(NewFileOption.CREATE, fileNameTextArea.getText());
        ((Stage) fileNameTextArea.getScene().getWindow()).close();
    }

    @FXML
    public void onCancelButtonClickEvent() {
        newFileResponse = new NewFileResponse(NewFileOption.CANCEL, null);
        ((Stage) fileNameTextArea.getScene().getWindow()).close();
    }

    @FXML
    public void onTextAreaKeyTyped(KeyEvent event) {
        if (fileNameTextArea.getText().trim().isEmpty()) {
            buttonCreate.setDisable(true);
        }
        else {
            buttonCreate.setDisable(false);
        }
    }

    @FXML
    public void onKeyPressed(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            if (!buttonCreate.isDisabled()) {
                newFileResponse = new NewFileResponse(NewFileOption.CREATE, fileNameTextArea.getText());
                ((Stage) fileNameTextArea.getScene().getWindow()).close();
            }
        }
    }
}
