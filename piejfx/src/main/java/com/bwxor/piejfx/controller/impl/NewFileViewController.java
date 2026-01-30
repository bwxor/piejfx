package com.bwxor.piejfx.controller.impl;

import com.bwxor.piejfx.controller.MovableViewController;
import com.bwxor.plugin.dto.NewFileResponse;
import com.bwxor.plugin.type.NewFileOption;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class NewFileViewController extends MovableViewController {
    @FXML
    private Label windowTitle;
    @FXML
    private TextField fileNameTextArea;
    @FXML
    private Button buttonCreate;

    private NewFileResponse newFileResponse;

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
