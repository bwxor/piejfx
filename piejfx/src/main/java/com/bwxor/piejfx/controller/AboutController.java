package com.bwxor.piejfx.controller;

import com.bwxor.piejfx.state.HostServicesState;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class AboutController {
    @FXML
    private AnchorPane titleBarAnchorPane;
    @FXML
    private Button closeButton;
    @FXML
    private Button minimizeButton;

    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    public void onCloseButtonClick() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
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
    public void onProjectWebsiteButtonClick(MouseEvent mouseEvent) {
        HostServicesState hostServicesState = HostServicesState.instance;

        hostServicesState.getHostServices().showDocument("https://bwxor.com/projects/piejfx");
    }

    @FXML
    public void onGitHubRepositoryButtonClick(MouseEvent mouseEvent) {
        HostServicesState hostServicesState = HostServicesState.instance;

        hostServicesState.getHostServices().showDocument("https://github.com/bwxor/piejfx");
    }
}
