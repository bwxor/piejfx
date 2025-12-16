package com.bwxor.piejfx.state;

import java.io.File;

public final class FolderTreeViewState {
    public static FolderTreeViewState instance = new FolderTreeViewState();

    private File openedFolder;

    private FolderTreeViewState() {
    }

    public File getOpenedFolder() {
        return openedFolder;
    }

    public void setOpenedFolder(File openedFolder) {
        this.openedFolder = openedFolder;
    }
}
