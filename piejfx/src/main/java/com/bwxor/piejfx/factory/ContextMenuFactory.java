package com.bwxor.piejfx.factory;

import com.bwxor.piejfx.utility.TerminalTabPaneUtility;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.text.Font;

public class ContextMenuFactory {
    public static ContextMenu createCodeAreaContextMenu(SplitPane verticalSplitPane, TabPane terminalTabPane) {
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.setStyle("-fx-font-family: " + Font.getDefault().getName());

        MenuItem showTerminalContextMenuItem = new MenuItem();
        showTerminalContextMenuItem.setText("Toggle Terminal Tab");
        showTerminalContextMenuItem.setOnAction(
                e -> {
                    TerminalTabPaneUtility.toggleTerminalTabPane(verticalSplitPane, terminalTabPane);
                }
        );

        contextMenu.getItems().add(showTerminalContextMenuItem);

        return contextMenu;
    }

    public static ContextMenu createTerminalTabPaneContextMenu(TabPane tabPane) {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem newTerminalTabMenuItem = new MenuItem();
        newTerminalTabMenuItem.setText("New Terminal Tab");
        newTerminalTabMenuItem.setOnAction(
                e -> {
                    TerminalTabPaneUtility.addTabToPane(tabPane, null);
                }
        );
        contextMenu.getItems().add(newTerminalTabMenuItem);

        MenuItem closeTerminalTabMenuItem = new MenuItem();
        closeTerminalTabMenuItem.setText("Close Terminal Tab");
        closeTerminalTabMenuItem.setOnAction(
                e -> {
                    TerminalTabPaneUtility.removeSelectedTabFromPane(tabPane);
                }
        );
        contextMenu.getItems().add(closeTerminalTabMenuItem);

        return contextMenu;
    }
}
