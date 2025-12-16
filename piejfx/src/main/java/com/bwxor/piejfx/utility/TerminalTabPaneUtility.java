package com.bwxor.piejfx.utility;

import com.bwxor.piejfx.factory.TabFactory;
import com.bwxor.piejfx.state.CodeAreaState;
import com.bwxor.piejfx.state.TerminalState;
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
    public static void addTabToPane(TabPane tabPane, String process) {
        Tab tab = TabFactory.createTerminalTab(process);
        tab.setOnCloseRequest(e -> {
            removeSelectedTabFromPane(tabPane);
            e.consume();
        });


        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    public static void removeSelectedTabFromPane(TabPane tabPane) {
        TerminalState.instance.getTerminals().remove(tabPane.getSelectionModel().getSelectedIndex());
        tabPane.getTabs().remove(tabPane.getSelectionModel().getSelectedItem());

        if (tabPane.getTabs().isEmpty()) {
            addTabToPane(tabPane, null);
        }
    }

    /**
     *
     * @param verticalSplitPane
     * @param terminalTabPane
     */
    public static void toggleTerminalTabPane(SplitPane verticalSplitPane, TabPane terminalTabPane) {
        if (verticalSplitPane.getItems().contains(terminalTabPane)) {
            verticalSplitPane.getItems().remove(terminalTabPane);
        } else {
            verticalSplitPane.getItems().add(terminalTabPane);
        }
    }
}
