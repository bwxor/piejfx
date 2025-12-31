package com.bwxor.plugin.input;

import javafx.scene.control.Menu;
import javafx.scene.control.TabPane;

public class ApplicationWindow {
    private TabPane sidebarTabPane;
    private TabPane editorTabPane;
    private Menu menu;


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

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }
}
