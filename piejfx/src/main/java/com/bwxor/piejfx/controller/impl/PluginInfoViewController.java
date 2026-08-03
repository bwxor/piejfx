package com.bwxor.piejfx.controller.impl;

import com.bwxor.piejfx.controller.MovableViewController;
import com.bwxor.piejfx.dto.FetchedPlugin;
import com.bwxor.piejfx.dto.LoadedPlugin;
import com.bwxor.piejfx.exception.PluginManagementServiceException;
import com.bwxor.piejfx.state.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class PluginInfoViewController extends MovableViewController {
    @FXML
    private Label pluginNameLabel;
    @FXML
    private Label pluginAuthorLabel;
    @FXML
    private Label pluginDescriptionLabel;
    private FetchedPlugin fetchedPlugin;

    public void loadPluginInformation(final FetchedPlugin fetchedPlugin) {
        this.fetchedPlugin = fetchedPlugin;

        pluginNameLabel.setText(fetchedPlugin.name());
        pluginAuthorLabel.setText(fetchedPlugin.author());
        pluginDescriptionLabel.setText(fetchedPlugin.description());
    }

    public void onDownloadPageButtonClick(MouseEvent mouseEvent) {
        HostServicesState hostServicesState = HostServicesState.instance;

        hostServicesState.getHostServices().showDocument(fetchedPlugin.url());
    }
}
