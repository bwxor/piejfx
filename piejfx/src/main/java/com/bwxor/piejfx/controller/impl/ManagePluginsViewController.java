package com.bwxor.piejfx.controller.impl;

import com.bwxor.piejfx.controller.MaximizableViewController;
import com.bwxor.piejfx.dto.FetchedPlugin;
import com.bwxor.piejfx.exception.PluginManagementServiceException;
import com.bwxor.piejfx.state.FetchedPluginsState;
import com.bwxor.piejfx.state.LoadedPluginsState;
import com.bwxor.piejfx.state.ServiceState;
import com.bwxor.piejfx.state.StageState;
import com.bwxor.piejfx.state.UIState;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ManagePluginsViewController extends MaximizableViewController {

    private enum FilterMode { ALL, INSTALLED, NOT_INSTALLED }

    @FXML private TextField pluginsTextField;
    @FXML private VBox      pluginVBox;
    @FXML private Button    filterAllButton;
    @FXML private Button    filterInstalledButton;
    @FXML private Button    filterNotInstalledButton;

    private FilterMode currentFilter = FilterMode.ALL;

    // -------------------------------------------------------------------------
    // Initialisation
    // -------------------------------------------------------------------------

    public void loadVBox() {
        // "All" is active by default
        setActiveFilter(FilterMode.ALL);
        applyFilter();
    }

    // -------------------------------------------------------------------------
    // Filter button actions (wired from FXML)
    // -------------------------------------------------------------------------

    @FXML
    public void onFilterAll() {
        setActiveFilter(FilterMode.ALL);
        applyFilter();
    }

    @FXML
    public void onFilterInstalled() {
        setActiveFilter(FilterMode.INSTALLED);
        applyFilter();
    }

    @FXML
    public void onFilterNotInstalled() {
        setActiveFilter(FilterMode.NOT_INSTALLED);
        applyFilter();
    }

    // -------------------------------------------------------------------------
    // Search field (wired from FXML)
    // -------------------------------------------------------------------------

    @FXML
    public void onTextChanged(KeyEvent keyEvent) {
        applyFilter();
    }

    // -------------------------------------------------------------------------
    // Core filtering
    // -------------------------------------------------------------------------

    /**
     * Returns true when a fetched plugin is present in LoadedPluginsState.
     * Matches by directory slug first, then by the loaded plugin's name against
     * the fetched slug or name — handles cases where the extracted directory
     * name differs from the API slug.
     */
    private boolean isPluginInstalled(FetchedPlugin p) {
        return LoadedPluginsState.instance.getPlugins().stream().anyMatch(l ->
                l.getSlug().equals(p.slug())
                        || l.getName().equals(p.slug())
                        || l.getName().equals(p.name()));
    }

    /** Rebuilds pluginVBox using the current text query AND the current filter mode. */
    private void applyFilter() {
        String trimmedText = pluginsTextField.getText().trim().toLowerCase();

        List<FetchedPlugin> candidates;
        if (trimmedText.startsWith("slug:")) {
            String slugQuery = trimmedText.substring(5);
            candidates = FetchedPluginsState.instance.getPlugins().stream()
                    .filter(e -> e.slug() != null && e.slug().toLowerCase().contains(slugQuery))
                    .toList();
        } else {
            candidates = FetchedPluginsState.instance.getPlugins().stream()
                    .filter(e -> trimmedText.isEmpty()
                            || e.name().toLowerCase().contains(trimmedText)
                            || (e.slug() != null && e.slug().toLowerCase().contains(trimmedText)))
                    .toList();
        }

        // Apply installation filter on top of the text result
        List<FetchedPlugin> filtered = switch (currentFilter) {
            case INSTALLED     -> candidates.stream().filter(this::isPluginInstalled).toList();
            case NOT_INSTALLED -> candidates.stream().filter(p -> !isPluginInstalled(p)).toList();
            case ALL           -> candidates;
        };

        pluginVBox.getChildren().clear();
        for (var p : filtered) {
            pluginVBox.getChildren().add(createVBox(p));
        }
    }

    /** Updates currentFilter, marks the active button as disabled, and re-enables the others. */
    private void setActiveFilter(FilterMode mode) {
        currentFilter = mode;

        // Re-enable all, then disable the active one so it cannot be re-clicked
        filterAllButton.setDisable(false);
        filterInstalledButton.setDisable(false);
        filterNotInstalledButton.setDisable(false);

        filterAllButton.getStyleClass().remove("filter-btn-active");
        filterInstalledButton.getStyleClass().remove("filter-btn-active");
        filterNotInstalledButton.getStyleClass().remove("filter-btn-active");

        switch (mode) {
            case ALL -> {
                filterAllButton.getStyleClass().add("filter-btn-active");
                filterAllButton.setDisable(true);
            }
            case INSTALLED -> {
                filterInstalledButton.getStyleClass().add("filter-btn-active");
                filterInstalledButton.setDisable(true);
            }
            case NOT_INSTALLED -> {
                filterNotInstalledButton.getStyleClass().add("filter-btn-active");
                filterNotInstalledButton.setDisable(true);
            }
        }
    }

    // -------------------------------------------------------------------------
    // Plugin row builder
    // -------------------------------------------------------------------------

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

        boolean isInstalled = isPluginInstalled(plugin);

        var operationButton = new Button(isInstalled ? "Uninstall" : "Install");
        var detailsButton   = new Button("Details");

        // Enable/Disable button — only active when the plugin is installed
        var enableButton = new Button();
        var loadedOpt = loadedPluginsState.getPlugins().stream()
                .filter(p -> p.getSlug().equals(plugin.slug())
                        || p.getName().equals(plugin.slug())
                        || p.getName().equals(plugin.name()))
                .findFirst();
        if (loadedOpt.isPresent()) {
            enableButton.setText(loadedOpt.get().isEnabled() ? "Disable" : "Enable");
            enableButton.setDisable(false);
        } else {
            enableButton.setText("Enable");
            enableButton.setDisable(true);
        }

        enableButton.setOnMouseClicked(_ -> {
            var currentOpt = LoadedPluginsState.instance.getPlugins().stream()
                    .filter(p -> p.getSlug().equals(plugin.slug())
                            || p.getName().equals(plugin.slug())
                            || p.getName().equals(plugin.name()))
                    .findFirst();
            if (currentOpt.isEmpty()) return;

            var loadedPlugin = currentOpt.get();
            boolean newEnabled = !loadedPlugin.isEnabled();

            loadedPlugin.setEnabled(newEnabled);
            ServiceState.instance.getPluginEnabledConfigService().setEnabled(loadedPlugin.getSlug(), newEnabled);

            enableButton.setText(newEnabled ? "Disable" : "Enable");

            // Reset: clear plugin-contributed UI elements and re-invoke onLoad for all enabled plugins
            UIState uiState = UIState.instance;
            while (uiState.getMenuBar().getMenus().size() > 3) {
                uiState.getMenuBar().getMenus().removeLast();
            }
            while (uiState.getSplitTabPane().getTabs().size() > 1) {
                uiState.getSplitTabPane().getTabs().removeLast();
            }
            ServiceState.instance.getPluginService().invokeOnLoad();
        });

        operationButton.setOnMouseClicked(_ -> {
            if (operationButton.getText().equals("Install")) {
                operationButton.setText("Installing...");
                operationButton.setDisable(true);
                detailsButton.setDisable(true);
                enableButton.setDisable(true);

                CompletableFuture.runAsync(() -> {
                    try {
                        serviceState.getPluginManagementService().installPlugin(plugin);
                        Platform.runLater(() ->
                                ServiceState.instance.getNotificationService().showNotificationOk(
                                        "Plugin '" + plugin.name() + "' installed successfully!", stageState.getManagePluginsStage()));
                    } catch (PluginManagementServiceException e) {
                        Platform.runLater(() -> ServiceState.instance.getNotificationService()
                                .showNotificationOk("Failed to install plugin: " + e.getMessage()));
                    }
                }).thenRun(() -> Platform.runLater(this::applyFilter));
            } else {
                operationButton.setText("Uninstalling...");
                operationButton.setDisable(true);
                detailsButton.setDisable(true);
                enableButton.setDisable(true);

                CompletableFuture.runAsync(() -> {
                    try {
                        serviceState.getPluginManagementService().uninstallPlugin(plugin);
                        Platform.runLater(() -> ServiceState.instance.getNotificationService()
                                .showNotificationOk("Plugin '" + plugin.name() + "' uninstalled successfully!", stageState.getManagePluginsStage()));
                    } catch (PluginManagementServiceException e) {
                        Platform.runLater(() -> ServiceState.instance.getNotificationService()
                                .showNotificationOk("Failed to uninstall plugin: " + e.getMessage()));
                    }
                }).thenRun(() -> Platform.runLater(this::applyFilter));
            }
        });

        operationButton.setContentDisplay(ContentDisplay.LEFT);
        hBox.getChildren().add(operationButton);

        detailsButton.setOnMouseClicked(_ ->
                serviceState.getPluginInfoViewService().showGetPluginsView(plugin));
        detailsButton.setContentDisplay(ContentDisplay.LEFT);

        hBox.getChildren().add(detailsButton);
        hBox.getChildren().add(enableButton);

        vBox.getChildren().add(hBox);

        return vBox;
    }
}
