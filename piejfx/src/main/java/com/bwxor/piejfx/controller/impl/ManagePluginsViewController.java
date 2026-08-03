package com.bwxor.piejfx.controller.impl;

import com.bwxor.piejfx.controller.MaximizableViewController;
import com.bwxor.piejfx.dto.FetchedPlugin;
import com.bwxor.piejfx.exception.PluginManagementServiceException;
import com.bwxor.piejfx.state.FetchedPluginsState;
import com.bwxor.piejfx.state.LoadedPluginsState;
import com.bwxor.piejfx.state.ServiceState;
import com.bwxor.piejfx.state.StageState;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.concurrent.CompletableFuture;

public class ManagePluginsViewController extends MaximizableViewController {
    @FXML
    private TextField pluginsTextField;
    @FXML
    private VBox pluginVBox;

    public void loadVBox() {
        for (var p : FetchedPluginsState.instance.getPlugins()) {
            pluginVBox.getChildren().add(createVBox(p));
        }
    }

    public void onTextChanged(KeyEvent keyEvent) {
        pluginVBox.getChildren().clear();

        String trimmedText = pluginsTextField.getText().trim();
        for (var p : FetchedPluginsState.instance.getPlugins().stream().filter(e -> e.name().contains(trimmedText)).toList()) {
            pluginVBox.getChildren().add(createVBox(p));
        }
    }

    private VBox createVBox(FetchedPlugin plugin) {
        ServiceState serviceState = ServiceState.instance;
        LoadedPluginsState loadedPluginsState = LoadedPluginsState.instance;
        StageState stageState = StageState.instance;

        var vBox = new VBox();
        vBox.setSpacing(5);

        var titleLabel = new Label(plugin.name());
        titleLabel.setStyle("-fx-font-size: 18px;");
        vBox.getChildren().add(titleLabel);

        var descriptionLabel = new Label(plugin.description());
        descriptionLabel.setStyle("-fx-text-fill: text-secondary;");
        descriptionLabel.setWrapText(true);
        vBox.getChildren().add(descriptionLabel);

        var hBox = new HBox();
        hBox.setSpacing(3);

        var operationButton = new Button();
        if (loadedPluginsState.getPlugins().stream().anyMatch(p -> p.getName().equals(plugin.name()))) {
            operationButton.setText("Uninstall");
        } else {
            operationButton.setText("Install");
        }

        var detailsButton = new Button("Details");

        operationButton.setOnMouseClicked(_ -> {
            if (operationButton.getText().equals("Install")) {
                operationButton.setText("Installing...");
                operationButton.setDisable(true);
                detailsButton.setDisable(true);

                CompletableFuture.runAsync(() -> {
                    try {
                        serviceState.getPluginManagementService().installPlugin(plugin);
                        Platform.runLater(() -> ServiceState.instance.getNotificationService().showNotificationOk("Plugin '" + plugin.name() + "' installed successfully!", stageState.getManagePluginsStage()));
                    } catch (PluginManagementServiceException e) {
                        Platform.runLater(() -> ServiceState.instance.getNotificationService().showNotificationOk("Failed to install plugin: " + e.getMessage()));
                    }
                }).thenRun(() ->
                        Platform.runLater(() -> {
                            operationButton.setText("Uninstall");
                            operationButton.setDisable(false);
                            detailsButton.setDisable(false);
                        }));
            } else {
                operationButton.setText("Uninstalling...");
                operationButton.setDisable(true);
                detailsButton.setDisable(true);

                CompletableFuture.runAsync(() -> {
                    try {
                        serviceState.getPluginManagementService().uninstallPlugin(plugin);
                        Platform.runLater(() -> ServiceState.instance.getNotificationService().showNotificationOk("Plugin '" + plugin.name() + "' uninstalled successfully!", stageState.getManagePluginsStage()));
                    } catch (PluginManagementServiceException e) {
                        Platform.runLater(() -> ServiceState.instance.getNotificationService().showNotificationOk("Failed to uninstall plugin: " + e.getMessage()));
                    }
                }).thenRun(() -> Platform.runLater(() -> {
                    operationButton.setText("Install");
                    operationButton.setDisable(false);
                    detailsButton.setDisable(false);
                }));
            }
        });


        operationButton.setContentDisplay(ContentDisplay.LEFT);
        hBox.getChildren().add(operationButton);

        detailsButton.setOnMouseClicked(_ -> {
            serviceState.getPluginInfoViewService().showGetPluginsView(plugin);
        });

        detailsButton.setContentDisplay(ContentDisplay.LEFT);

        hBox.getChildren().add(detailsButton);

        vBox.getChildren().add(hBox);

        return vBox;
    }
}
