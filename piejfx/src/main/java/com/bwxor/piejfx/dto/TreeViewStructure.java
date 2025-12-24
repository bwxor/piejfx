package com.bwxor.piejfx.dto;

import java.io.File;
import java.util.List;

/**
 * A TreeViewStructure singleton will be used in order to memorize the expansion state of the folder browser view.
 * Whenever the TreeView gets refreshed (e.g. a new file is created and needs to be updated in the view), every TreeItem
 * will get shrunk. Such a recursive object helps the application programmatically rebuild its previous structure.
 */
public class TreeViewStructure {
    private File file;
    private List<TreeViewStructure> children;
    private boolean isExpanded;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public List<TreeViewStructure> getChildren() {
        return children;
    }

    public void setChildren(List<TreeViewStructure> children) {
        this.children = children;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
}
