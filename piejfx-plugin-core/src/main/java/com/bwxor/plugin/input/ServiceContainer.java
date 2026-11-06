package com.bwxor.plugin.input;

import com.bwxor.plugin.service.*;

public class ServiceContainer {
    private PluginCloseService closeService;
    private PluginEditorTabPaneService editorTabPaneService;
    private PluginFolderTreeViewService folderTreeViewService;
    private PluginNotificationService notificationService;
    private PluginFileService fileService;
    private PluginTerminalTabPaneService pluginTerminalTabPaneService;
    private PluginSyntaxErrorService pluginSyntaxErrorService;

    public ServiceContainer(PluginCloseService closeService, PluginEditorTabPaneService editorTabPaneService, PluginFolderTreeViewService folderTreeViewService, PluginNotificationService notificationService, PluginFileService fileService, PluginTerminalTabPaneService pluginTerminalTabPaneService, PluginSyntaxErrorService pluginSyntaxErrorService) {
        this.closeService = closeService;
        this.editorTabPaneService = editorTabPaneService;
        this.folderTreeViewService = folderTreeViewService;
        this.notificationService = notificationService;
        this.fileService = fileService;
        this.pluginTerminalTabPaneService = pluginTerminalTabPaneService;
        this.pluginSyntaxErrorService = pluginSyntaxErrorService;
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

    public PluginFileService getFileService() {
        return fileService;
    }

    public PluginTerminalTabPaneService getPluginTerminalTabPaneService() {
        return pluginTerminalTabPaneService;
    }

    public PluginSyntaxErrorService getPluginSyntaxErrorService() {
        return pluginSyntaxErrorService;
    }
}
