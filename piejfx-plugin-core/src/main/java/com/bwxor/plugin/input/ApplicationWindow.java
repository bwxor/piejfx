package com.bwxor.plugin.input;

import javafx.scene.control.MenuBar;
import javafx.scene.control.TabPane;

public class ApplicationWindow {
    private TabPane sidebarTabPane;
    private TabPane editorTabPane;
    private MenuBar menuBar;


    public TabPane getSidebarTabPane() {
        return sidebarTabPane;
    }

    public void setSidebarTabPane(TabPane sidebarTabPane) {
        this.sidebarTabPane = sidebarTabPane;
    }

    public TabPane getEditorTabPane() {
        return editorTabPane;
    }

    public void setEditorTabPane(TabPane editorTabPane) {
        this.editorTabPane = editorTabPane;
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }

    public void setMenuBar(MenuBar menuBar) {
        this.menuBar = menuBar;
    }
}
