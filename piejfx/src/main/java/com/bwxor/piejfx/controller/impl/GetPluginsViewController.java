package com.bwxor.piejfx.controller.impl;

import com.bwxor.piejfx.controller.MaximizableViewController;
import com.bwxor.piejfx.dto.FetchedPlugin;
import com.bwxor.piejfx.state.FetchedPluginsState;
import com.bwxor.piejfx.state.ServiceState;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class GetPluginsViewController extends MaximizableViewController {
    @FXML
    private ListView<FetchedPlugin> pluginListView;
    @FXML
    public TextField pluginsTextField;

    public void loadVBox() {
        for (var p : FetchedPluginsState.instance.getPlugins()) {
            pluginListView.getItems().add(p);
        }
    }

    public void onTextChanged(KeyEvent keyEvent) {
        pluginListView.getItems().clear();

        String trimmedText = pluginsTextField.getText().trim();
        for (var p : FetchedPluginsState.instance.getPlugins().stream().filter(
                e -> e.name().contains(trimmedText)).toList()) {
            pluginListView.getItems().add(p);
        }
    }

    public void handlePluginListViewClick(MouseEvent mouseEvent) {
        ServiceState serviceState = ServiceState.instance;

        if (mouseEvent.getClickCount() == 2) {
            var selectedPlugin = pluginListView.getSelectionModel().getSelectedItem();
            serviceState.getPluginInfoViewService().showGetPluginsView(selectedPlugin);
        }
    }
}
