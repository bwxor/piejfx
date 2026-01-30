package com.bwxor.piejfx.controller;

import com.bwxor.piejfx.state.MaximizeState;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class MaximizableViewController extends MovableViewController {
    @FXML
    private Button maximizeButton;

    @FXML
    public void onMaximizeButtonClick() {
        MaximizeState.instance.toggleMaximize((Stage)maximizeButton.getScene().getWindow(), maximizeButton);
    }

    @FXML
    public void handleDoubleClickAction(MouseEvent mouseEvent) {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            if (mouseEvent.getClickCount() == 2) {
                MaximizeState.instance.toggleMaximize((Stage)maximizeButton.getScene().getWindow(), maximizeButton);
            }
        }
    }
}
