package com.bwxor.piejfx.utility;

import com.bwxor.piejfx.factory.TabFactory;
import com.bwxor.piejfx.state.CodeAreaState;
import com.bwxor.piejfx.state.TerminalState;
import com.bwxor.piejfx.state.UIState;
import com.bwxor.piejfx.type.NotificationYesNoCancelOption;
import com.bwxor.piejfx.type.RemoveSelectedTabFromPaneResponse;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import org.fxmisc.richtext.CodeArea;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class TerminalTabPaneUtility {
    public static void addTabToPane(String process) {
        UIState uiState = UIState.getInstance();

        Tab tab = TabFactory.createTerminalTab(process);
        tab.setOnCloseRequest(e -> {
            removeSelectedTabFromPane(uiState.getTerminalTabPane());
            e.consume();
        });


        uiState.getTerminalTabPane().getTabs().add(tab);
        uiState.getTerminalTabPane().getSelectionModel().select(tab);
    }

    public static void removeSelectedTabFromPane(TabPane tabPane) {
        TerminalState.instance.getTerminals().remove(tabPane.getSelectionModel().getSelectedIndex());
        tabPane.getTabs().remove(tabPane.getSelectionModel().getSelectedItem());

        if (tabPane.getTabs().isEmpty()) {
            addTabToPane(null);
        }
    }

    public static void toggleTerminalTabPane() {
        UIState uiState = UIState.getInstance();

        if (uiState.getVerticalSplitPane().getItems().contains(uiState.getTerminalTabPane())) {
            uiState.getVerticalSplitPane().getItems().remove(uiState.getTerminalTabPane());
        } else {
            uiState.getVerticalSplitPane().getItems().add(uiState.getTerminalTabPane());
        }
    }
}
