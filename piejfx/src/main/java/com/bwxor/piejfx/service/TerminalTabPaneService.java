package com.bwxor.piejfx.service;

import com.bwxor.piejfx.factory.TabFactory;
import com.bwxor.piejfx.state.TerminalState;
import com.bwxor.piejfx.state.UIState;
import com.bwxor.plugin.service.PluginTerminalTabPaneService;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class TerminalTabPaneService implements PluginTerminalTabPaneService {
    public void addTabToPane(String process) {
        UIState uiState = UIState.instance;

        Tab tab = TabFactory.createTerminalTab(process);
        tab.setOnCloseRequest(e -> {
            removeSelectedTabFromPane(uiState.getTerminalTabPane());
            e.consume();
        });


        uiState.getTerminalTabPane().getTabs().add(tab);
        uiState.getTerminalTabPane().getSelectionModel().select(tab);
    }

    public void removeSelectedTabFromPane(TabPane tabPane) {
        TerminalState.instance.getTerminals().remove(tabPane.getSelectionModel().getSelectedIndex());
        tabPane.getTabs().remove(tabPane.getSelectionModel().getSelectedItem());

        if (tabPane.getTabs().isEmpty()) {
            addTabToPane(null);
        }
    }

    public void toggleTerminalTabPane() {
        UIState uiState = UIState.instance;

        if (uiState.getVerticalSplitPane().getItems().contains(uiState.getTerminalTabPane())) {
            uiState.getVerticalSplitPane().getItems().remove(uiState.getTerminalTabPane());
        } else {
            uiState.getVerticalSplitPane().getItems().add(uiState.getTerminalTabPane());
        }
    }
}
