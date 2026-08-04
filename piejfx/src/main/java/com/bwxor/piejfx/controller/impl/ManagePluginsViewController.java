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

        String trimmedText = pluginsTextField.getText().trim().toLowerCase();
        final java.util.List<FetchedPlugin> filtered;
        if (trimmedText.startsWith("slug:")) {
            String slugQuery = trimmedText.substring(5);
            filtered = FetchedPluginsState.instance.getPlugins().stream()
                    .filter(e -> e.slug() != null && e.slug().toLowerCase().contains(slugQuery))
                    .toList();
        } else {
            filtered = FetchedPluginsState.instance.getPlugins().stream()
                    .filter(e -> e.name().toLowerCase().contains(trimmedText)
                            || (e.slug() != null && e.slug().toLowerCase().contains(trimmedText)))
                    .toList();
        }
        for (var p : filtered) {
            pluginVBox.getChildren().add(createVBox(p));
        }
    }

    private static final int DESCRIPTION_MAX_CHARS = 150;

    private VBox createVBox(FetchedPlugin plugin) {
        ServiceState serviceState = ServiceState.instance;
        LoadedPluginsState loadedPluginsState = LoadedPluginsState.instance;
        StageState stageState = StageState.instance;

        var vBox = new VBox();
        vBox.setSpacing(5);

        var nameRow = new javafx.scene.layout.HBox();
        nameRow.setSpacing(6);
        nameRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        var titleLabel = new Label(plugin.name());
        titleLabel.setStyle("-fx-font-size: 18px;");
        nameRow.getChildren().add(titleLabel);

        if (plugin.verified()) {
            var verifiedLabel = new Label("\u2713 Verified");
            verifiedLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #4caf50; -fx-font-weight: bold;");
            nameRow.getChildren().add(verifiedLabel);
        }

        vBox.getChildren().add(nameRow);

        if (plugin.slug() != null && !plugin.slug().isBlank()) {
            var slugLabel = new Label(plugin.slug());
            slugLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: text-secondary;");
            vBox.getChildren().add(slugLabel);
        }

        String rawDesc = plugin.description() != null ? plugin.description() : "";
        String cappedDesc = rawDesc.length() > DESCRIPTION_MAX_CHARS
                ? rawDesc.substring(0, DESCRIPTION_MAX_CHARS) + "..."
                : rawDesc;
        var descriptionLabel = new Label(cappedDesc);
        descriptionLabel.setStyle("-fx-text-fill: text-secondary;");
        descriptionLabel.setWrapText(true);
        vBox.getChildren().add(descriptionLabel);

        var hBox = new HBox();
        hBox.setSpacing(3);

        var operationButton = new Button();
        if (loadedPluginsState.getPlugins().stream().anyMatch(p -> p.getName().equals(plugin.slug()))) {
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
