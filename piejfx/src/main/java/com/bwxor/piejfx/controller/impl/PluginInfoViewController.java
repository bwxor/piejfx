package com.bwxor.piejfx.controller.impl;

import com.bwxor.piejfx.controller.MovableViewController;
import com.bwxor.piejfx.dto.FetchedPlugin;
import com.bwxor.piejfx.dto.LoadedPlugin;
import com.bwxor.piejfx.state.HostServicesState;
import com.bwxor.piejfx.state.LoadedPluginsState;
import com.bwxor.piejfx.state.ServiceState;
import com.bwxor.piejfx.type.PluginOperation;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import java.util.List;

public class PluginInfoViewController extends MovableViewController {
    @FXML
    private Label pluginNameLabel;
    @FXML
    private Label pluginAuthorLabel;
    @FXML
    private Label pluginDescriptionLabel;
    @FXML
    private Button pluginOperationButton;
    private FetchedPlugin fetchedPlugin;
    private PluginOperation pluginOperation;

    public void loadPluginInformation(final FetchedPlugin fetchedPlugin) {
        LoadedPluginsState loadedPluginsState = LoadedPluginsState.instance;

        this.fetchedPlugin = fetchedPlugin;

        pluginNameLabel.setText(fetchedPlugin.name());
        pluginAuthorLabel.setText(fetchedPlugin.author());
        pluginDescriptionLabel.setText(fetchedPlugin.description());

        List<LoadedPlugin> loadedPlugins = loadedPluginsState.getPlugins();

        if (loadedPlugins.stream().anyMatch(p -> p.getName().equals(fetchedPlugin.name()))) {
            pluginOperation = PluginOperation.UNINSTALL;
            pluginOperationButton.setText("Uninstall");
        }
        else {
            pluginOperation = PluginOperation.INSTALL;
            pluginOperationButton.setText("Install");
        }
    }

    public void onPluginOperationButtonClick(MouseEvent mouseEvent) {

    }

    public void onDownloadPageButtonClick(MouseEvent mouseEvent) {
        HostServicesState hostServicesState = HostServicesState.instance;

        hostServicesState.getHostServices().showDocument(fetchedPlugin.url());
    }
}
