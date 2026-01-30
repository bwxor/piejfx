package com.bwxor.piejfx.controller.impl;

import com.bwxor.piejfx.controller.MovableViewController;
import com.bwxor.piejfx.state.HostServicesState;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

public class AboutViewController extends MovableViewController {
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
