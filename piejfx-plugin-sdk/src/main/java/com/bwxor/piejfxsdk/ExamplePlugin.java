package com.bwxor.piejfxsdk;

import com.bwxor.plugin.Plugin;
import com.bwxor.plugin.input.PluginContext;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.File;

public class ExamplePlugin implements Plugin {
    private PluginContext pluginContext;

    @Override
    public void onLoad(PluginContext pluginContext) {
        this.pluginContext = pluginContext;

        // ToDo: Edit or remove
        // Example: Adding a new tab to the sidebar
        TabPane tabPane = pluginContext.getApplicationWindow().getSidebarTabPane();
        tabPane.getTabs().add(new Tab("Example Plugin"));

        // ToDo: Edit or remove
        // Example: Adding a new menu item
        Menu menu = pluginContext.getApplicationWindow().getMenu();
        menu.getItems().add(new MenuItem("Example Plugin"));
    }

    @Override
    public void onKeyPress(KeyEvent keyEvent) {
        // ToDo: Edit or remove
        // Example: Adding a custom plugin hook
        if (keyEvent.isControlDown()) {
            if (keyEvent.getCode().equals(KeyCode.J)) {
                pluginContext.getNotificationService().showNotificationOk("Default hook for the CTRL + J hotkey");
            }
        }
    }

    @Override
    public void onSaveFile(File file) {
        // ToDo: Handle hook
    }

    @Override
    public void onOpenFile(File file) {
        // ToDo: Handle hook
    }

    @Override
    public void onCreateFile(File file) {
        // ToDo: Handle hook
    }

    @Override
    public void onCreateFolder(File file) {
        // ToDo: Handle hook
    }

    @Override
    public void onDeleteFile(File file) {
        // ToDo: Handle hook
    }
}
