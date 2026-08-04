package com.bwxor.piejfx.controller.impl;

import com.bwxor.piejfx.controller.MovableViewController;
import com.bwxor.piejfx.dto.FetchedPlugin;
import com.bwxor.piejfx.state.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;

public class PluginInfoViewController extends MovableViewController {
    @FXML
    private Label pluginNameLabel;
    @FXML
    private TextField pluginSlugField;
    @FXML
    private Label pluginAuthorLabel;
    @FXML
    private Label pluginDescriptionLabel;
    @FXML
    private Label pluginVerifiedLabel;
    private FetchedPlugin fetchedPlugin;

    public void loadPluginInformation(final FetchedPlugin fetchedPlugin) {
        this.fetchedPlugin = fetchedPlugin;

        pluginNameLabel.setText(fetchedPlugin.name());
        pluginAuthorLabel.setText("by " + (fetchedPlugin.author() != null ? fetchedPlugin.author() : "Unknown"));
        pluginSlugField.setText(fetchedPlugin.slug() != null ? fetchedPlugin.slug() : "");
        pluginDescriptionLabel.setText(fetchedPlugin.description());

        if (fetchedPlugin.verified()) {
            pluginVerifiedLabel.setText("\u2714 The plugin is verified");
            pluginVerifiedLabel.setStyle("-fx-text-fill: #4caf50; -fx-font-weight: bold;");
        } else {
            pluginVerifiedLabel.setText("\u2718 The plugin is not verified");
            pluginVerifiedLabel.setStyle("-fx-text-fill: #e53935; -fx-font-weight: bold;");
        }
    }

    public void onCopySlugButtonClick(MouseEvent mouseEvent) {
        ClipboardContent content = new ClipboardContent();
        content.putString(pluginSlugField.getText());
        Clipboard.getSystemClipboard().setContent(content);
    }

    public void onHomepageButtonClick(MouseEvent mouseEvent) {
        HostServicesState hostServicesState = HostServicesState.instance;
        hostServicesState.getHostServices().showDocument(fetchedPlugin.homepageUrl());
    }
}
