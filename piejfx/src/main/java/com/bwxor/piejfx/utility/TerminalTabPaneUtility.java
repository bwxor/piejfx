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
     * @param splitPane
     * @param terminalTabPane
     * @return true if the terminal tab pane is still visible, and false otherwise
     */
    public static boolean toggleTerminalTabPane(SplitPane splitPane, TabPane terminalTabPane) {
        if (splitPane.getItems().contains(terminalTabPane)) {
            splitPane.getItems().remove(terminalTabPane);
            return false;
        } else {
            splitPane.getItems().add(terminalTabPane);
            return true;
        }
    }
}
