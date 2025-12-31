package com.bwxor.plugin;

import com.bwxor.plugin.input.PluginContext;
import javafx.scene.input.KeyCode;

import java.io.File;

public interface Plugin {
    void onLoad(PluginContext pluginContext);
    void onOpenFile(File file);
    void onSaveFile();
    void onKeyPress(KeyCode c);
    void onCreateFile(File file);
    void onDeleteFile(File file);
    void onCreateFolder(File file);
}
