package com.bwxor.piejfx.controller.impl;

import com.bwxor.piejfx.controller.MovableViewController;
import com.bwxor.plugin.dto.NewFileResponse;
import com.bwxor.plugin.dto.RenameFileResponse;
import com.bwxor.plugin.type.NewFileOption;
import com.bwxor.plugin.type.RenameFileOption;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class RenameFileViewController extends MovableViewController {
    @FXML
    private TextField fileNameTextArea;
    @FXML
    private Button buttonRename;
    private String oldName;

    private RenameFileResponse renameFileResponse;


    public void initialize() {
        buttonRename.setDisable(true);
        Platform.runLater(() -> fileNameTextArea.requestFocus());
    }

    public void setRenameFileResponse(RenameFileResponse renameFileResponse) {
        this.renameFileResponse = renameFileResponse;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }

    public RenameFileResponse getRenameFileResponse() {
        return renameFileResponse;
    }

    @FXML
    public void onCloseButtonClick() {
        renameFileResponse = new RenameFileResponse(RenameFileOption.CANCEL, null, null);
        ((Stage) fileNameTextArea.getScene().getWindow()).close();
    }

    @FXML
    public void onRenameButtonClickEvent() {
        renameFileResponse = new RenameFileResponse(RenameFileOption.RENAME, oldName, fileNameTextArea.getText());
        ((Stage) fileNameTextArea.getScene().getWindow()).close();
    }

    @FXML
    public void onCancelButtonClickEvent() {
        renameFileResponse = new RenameFileResponse(RenameFileOption.CANCEL, null, null);
        ((Stage) fileNameTextArea.getScene().getWindow()).close();
    }

    @FXML
    public void onTextAreaKeyTyped(KeyEvent event) {
        if (fileNameTextArea.getText().trim().isEmpty()) {
            buttonRename.setDisable(true);
        }
        else {
            buttonRename.setDisable(false);
        }
    }

    @FXML
    public void onKeyPressed(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            if (!buttonRename.isDisabled()) {
                renameFileResponse = new RenameFileResponse(RenameFileOption.RENAME, oldName, fileNameTextArea.getText());
                ((Stage) fileNameTextArea.getScene().getWindow()).close();
            }
        }
    }
}
