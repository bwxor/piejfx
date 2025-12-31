package com.bwxor.plugin;

import com.bwxor.plugin.input.PluginContext;
import javafx.scene.input.KeyEvent;

import java.io.File;

public interface Plugin {
    void onLoad(PluginContext pluginContext);
    void onKeyPress(KeyEvent keyEvent);
    void onSaveFile(File file);
    void onOpenFile(File file);
    void onCreateFile(File file);
    void onCreateFolder(File file);
    void onDeleteFile(File file);
}
