package com.bwxor.piejfx.control;

import javafx.scene.control.TreeItem;

import java.io.File;

public class FileTreeItem extends TreeItem<String> {
    private File file;

    public FileTreeItem() {}

    public FileTreeItem(String fileName, File file) {
        super(fileName);
        this.file = file;
    }

    public File getFile() {
        return file;
    }
}
