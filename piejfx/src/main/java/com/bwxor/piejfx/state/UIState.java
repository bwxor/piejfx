package com.bwxor.piejfx.state;

import javafx.scene.control.*;

public class UIState {
    private SplitPane horizontalSplitPane;
    private SplitPane verticalSplitPane;
    private TabPane splitTabPane;
    private TreeView folderTreeView;
    private TabPane editorTabPane;
    private TabPane terminalTabPane;
    private Label titleBarLabel;
    private Menu themesMenu;
    private Menu pluginsMenu;
    public static final UIState instance = new UIState();

    private UIState() {
    }

    public SplitPane getHorizontalSplitPane() {
        return horizontalSplitPane;
    }

    public void setHorizontalSplitPane(SplitPane horizontalSplitPane) {
        this.horizontalSplitPane = horizontalSplitPane;
    }

    public SplitPane getVerticalSplitPane() {
        return verticalSplitPane;
    }

    public void setVerticalSplitPane(SplitPane verticalSplitPane) {
        this.verticalSplitPane = verticalSplitPane;
    }

    public TabPane getSplitTabPane() {
        return splitTabPane;
    }

    public void setSplitTabPane(TabPane splitTabPane) {
        this.splitTabPane = splitTabPane;
    }

    public TreeView getFolderTreeView() {
        return folderTreeView;
    }

    public void setFolderTreeView(TreeView folderTreeView) {
        this.folderTreeView = folderTreeView;
    }

    public TabPane getEditorTabPane() {
        return editorTabPane;
    }

    public void setEditorTabPane(TabPane editorTabPane) {
        this.editorTabPane = editorTabPane;
    }

    public TabPane getTerminalTabPane() {
        return terminalTabPane;
    }

    public void setTerminalTabPane(TabPane terminalTabPane) {
        this.terminalTabPane = terminalTabPane;
    }

    public Label getTitleBarLabel() {
        return titleBarLabel;
    }

    public void setTitleBarLabel(Label titleBarLabel) {
        this.titleBarLabel = titleBarLabel;
    }

    public Menu getThemesMenu() {
        return themesMenu;
    }

    public void setThemesMenu(Menu themesMenu) {
        this.themesMenu = themesMenu;
    }

    public Menu getPluginsMenu() {
        return pluginsMenu;
    }

    public void setPluginsMenu(Menu pluginsMenu) {
        this.pluginsMenu = pluginsMenu;
    }
}
