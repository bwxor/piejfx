package com.bwxor.piejfx.controller.impl;

import com.bwxor.piejfx.controller.MovableViewController;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class NotificationOkViewController extends MovableViewController {
    @FXML
    private TextArea notificationText;

    public void setNotificationText(String text) {
        notificationText.setText(text);
    }

    @FXML
    public void onOkButtonClickEvent() {
        super.onCloseButtonClick();
    }
}
