package com.bwxor.piejfx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class NotificationOkController {
    @FXML
    private AnchorPane titleBarAnchorPane;
    @FXML
    private Button closeButton;
    @FXML
    private Button minimizeButton;
    @FXML
    private TextArea notificationText;
    @FXML
    private Button buttonOk;
    private double xOffset = 0;
    private double yOffset = 0;

    public void setNotificationText(String text) {
        notificationText.setText(text);
    }

    @FXML
    public void onCloseButtonClick() {
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
    public void onOkButtonClickEvent() {
        ((Stage) notificationText.getScene().getWindow()).close();
    }
}
