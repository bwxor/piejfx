package com.bwxor.piejfx.state;

import com.bwxor.piejfx.dto.TreeViewStructure;

import java.io.File;

public final class FolderTreeViewState {
    public static FolderTreeViewState instance = new FolderTreeViewState();

    private File openedFolder;
    private FolderTreeViewState parent;
    private TreeViewStructure treeViewStructure;

    private FolderTreeViewState() {
    }

    public File getOpenedFolder() {
        return openedFolder;
    }

    public void setOpenedFolder(File openedFolder) {
        this.openedFolder = openedFolder;
    }

    public FolderTreeViewState getParent() {
        return parent;
    }

    public void setParent(FolderTreeViewState parent) {
        this.parent = parent;
    }

    public TreeViewStructure getTreeViewStructure() {
        return treeViewStructure;
    }

    public void setTreeViewStructure(TreeViewStructure treeViewStructure) {
        this.treeViewStructure = treeViewStructure;
    }
}
