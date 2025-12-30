package com.bwxor.piejfx.controller;

import com.bwxor.plugin.type.NotificationYesNoCancelOption;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class NotificationYesNoCancelController {
    @FXML
    private AnchorPane titleBarAnchorPane;
    @FXML
    private Button closeButton;
    @FXML
    private Button minimizeButton;
    @FXML
    private TextArea notificationText;
    @FXML
    private Button buttonYes;
    @FXML
    private Button buttonNo;
    @FXML
    private Button buttonCancel;
    private NotificationYesNoCancelOption pickedOption;
    private double xOffset = 0;
    private double yOffset = 0;

    public NotificationYesNoCancelOption getPickedOption() {
        return pickedOption;
    }

    public void setPickedOption(NotificationYesNoCancelOption pickedOption) {
        this.pickedOption = pickedOption;
    }

    public void setNotificationText(String text) {
        notificationText.setText(text);
    }

    @FXML
    public void onCloseButtonClick() {
        pickedOption = NotificationYesNoCancelOption.CANCEL;
        ((Stage) notificationText.getScene().getWindow()).close();
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
    public void onYesButtonClickEvent() {
        pickedOption = NotificationYesNoCancelOption.YES;
        ((Stage) notificationText.getScene().getWindow()).close();
    }

    @FXML
    public void onNoButtonClickEvent() {
        pickedOption = NotificationYesNoCancelOption.NO;
        ((Stage) notificationText.getScene().getWindow()).close();
    }

    @FXML
    public void onCancelButtonClickEvent() {
        pickedOption = NotificationYesNoCancelOption.CANCEL;
        ((Stage) notificationText.getScene().getWindow()).close();
    }
}
