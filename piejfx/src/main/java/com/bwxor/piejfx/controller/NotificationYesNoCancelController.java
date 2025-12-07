package com.bwxor.piejfx.controller;

import com.bwxor.piejfx.type.NotificationYesNoCancelOption;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class NotificationYesNoCancelController {
    @FXML
    private TextArea notificationText;
    @FXML
    private Button buttonYes;
    @FXML
    private Button buttonNo;
    @FXML
    private Button buttonCancel;
    private NotificationYesNoCancelOption pickedOption;

    public NotificationYesNoCancelOption getPickedOption() {
        return pickedOption;
    }

    public void setNotificationText(String text) {
        notificationText.setText(text);
    }

    @FXML
    public void onYesButtonClickEvent() {
        pickedOption = NotificationYesNoCancelOption.YES;
        ((Stage)notificationText.getScene().getWindow()).close();
    }

    @FXML
    public void onNoButtonClickEvent() {
        pickedOption = NotificationYesNoCancelOption.NO;
        ((Stage)notificationText.getScene().getWindow()).close();
    }

    @FXML
    public void onCancelButtonClickEvent() {
        pickedOption = NotificationYesNoCancelOption.CANCEL;
        ((Stage)notificationText.getScene().getWindow()).close();
    }
}
