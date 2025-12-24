package com.bwxor.piejfx.control;

import javafx.scene.control.TreeItem;

import java.io.File;

/**
 * Custom TreeItem class, used for the "Open Folder" view, that includes a reference to a certain file from the open
 * folder. The text property wouldn't be enough, as it only keeps the name of the file (without its absolute path).
 */
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
