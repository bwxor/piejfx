package com.bwxor.plugin;

import javafx.scene.input.KeyCode;

import java.io.File;

public interface Plugin {
    void onLoad(ApplicationWindow applicationWindow);
    void onOpenFile(File file);
    void onSaveFile();
    void onKeyPress(KeyCode c);
}
