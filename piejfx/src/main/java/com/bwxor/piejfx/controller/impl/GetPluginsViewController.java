package com.bwxor.piejfx.controller.impl;

import com.bwxor.piejfx.controller.MaximizableViewController;
import com.bwxor.piejfx.dto.FetchedPlugin;
import com.bwxor.piejfx.state.FetchedPluginsState;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

public class GetPluginsViewController extends MaximizableViewController {
    @FXML
    private ListView pluginListView;
    @FXML
    public TextField pluginsTextField;

    public void loadVBox() {
        for (var p : FetchedPluginsState.instance.getPlugins()) {
            pluginListView.getItems().add(p.name());
        }
    }

    public void onTextChanged(KeyEvent keyEvent) {
        pluginListView.getItems().clear();

        String trimmedText = pluginsTextField.getText().trim();
        for (var p : FetchedPluginsState.instance.getPlugins().stream().filter(
                e -> e.name().contains(trimmedText)).toList()) {
            pluginListView.getItems().add(p.name());
        }
    }
}
