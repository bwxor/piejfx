package com.bwxor.piejfx.dto;

import java.io.File;
import java.util.List;

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
