package com.bwxor.piejfx.controller.impl;

import com.bwxor.piejfx.controller.MovableViewController;
import com.bwxor.plugin.type.NotificationYesNoCancelOption;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class NotificationYesNoCancelViewController extends MovableViewController {
    @FXML
    private TextArea notificationText;

    private NotificationYesNoCancelOption pickedOption;

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
        super.onCloseButtonClick();
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
