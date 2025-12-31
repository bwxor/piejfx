package com.bwxor.plugin.input;

import com.bwxor.plugin.service.*;

public class PluginContext {
    private ApplicationWindow applicationWindow;
    private PluginCloseService closeService;
    private PluginEditorTabPaneService editorTabPaneService;
    private PluginFolderTreeViewService folderTreeViewService;
    private PluginNotificationService notificationService;
    private PluginFileService fileService;
    private PluginTerminalTabPaneService pluginTerminalTabPaneService;

    public PluginContext(ApplicationWindow applicationWindow, PluginCloseService closeService, PluginEditorTabPaneService editorTabPaneService, PluginFolderTreeViewService folderTreeViewService, PluginNotificationService notificationService, PluginFileService fileService, PluginTerminalTabPaneService pluginTerminalTabPaneService) {
        this.applicationWindow = applicationWindow;
        this.closeService = closeService;
        this.editorTabPaneService = editorTabPaneService;
        this.folderTreeViewService = folderTreeViewService;
        this.notificationService = notificationService;
        this.fileService = fileService;
        this.pluginTerminalTabPaneService = pluginTerminalTabPaneService;
    }

    public ApplicationWindow getApplicationWindow() {
        return applicationWindow;
    }

    public PluginCloseService getCloseService() {
        return closeService;
    }

    public PluginEditorTabPaneService getEditorTabPaneService() {
        return editorTabPaneService;
    }

    public PluginFolderTreeViewService getFolderTreeViewService() {
        return folderTreeViewService;
    }

    public PluginNotificationService getNotificationService() {
        return notificationService;
    }

    public PluginFileService getOpenFolderService() {
        return fileService;
    }

    public PluginTerminalTabPaneService getPluginTerminalTabPaneService() {
        return pluginTerminalTabPaneService;
    }
}
