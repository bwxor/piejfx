package com.bwxor.plugin;

import com.bwxor.plugin.service.*;

public class PluginContext {
    private ApplicationWindow applicationWindow;
    private PluginCloseService closeService;
    private PluginEditorTabPaneService editorTabPaneService;
    private PluginFileOperationsService fileOperationsService;
    private PluginFolderTreeViewService folderTreeViewService;
    private PluginNotificationService notificationService;
    private PluginOpenFileService openFileService;
    private PluginOpenFolderService openFolderService;
    private PluginSaveFileService saveFileService;
    private PluginTerminalTabPaneService pluginTerminalTabPaneService;

    public PluginContext(ApplicationWindow applicationWindow, PluginCloseService closeService, PluginEditorTabPaneService editorTabPaneService, PluginFileOperationsService fileOperationsService, PluginFolderTreeViewService folderTreeViewService, PluginNotificationService notificationService, PluginOpenFileService openFileService, PluginOpenFolderService openFolderService, PluginSaveFileService saveFileService, PluginTerminalTabPaneService pluginTerminalTabPaneService) {
        this.applicationWindow = applicationWindow;
        this.closeService = closeService;
        this.editorTabPaneService = editorTabPaneService;
        this.fileOperationsService = fileOperationsService;
        this.folderTreeViewService = folderTreeViewService;
        this.notificationService = notificationService;
        this.openFileService = openFileService;
        this.openFolderService = openFolderService;
        this.saveFileService = saveFileService;
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

    public PluginFileOperationsService getFileOperationsService() {
        return fileOperationsService;
    }

    public PluginFolderTreeViewService getFolderTreeViewService() {
        return folderTreeViewService;
    }

    public PluginNotificationService getNotificationService() {
        return notificationService;
    }

    public PluginOpenFileService getOpenFileService() {
        return openFileService;
    }

    public PluginOpenFolderService getOpenFolderService() {
        return openFolderService;
    }

    public PluginSaveFileService getSaveFileService() {
        return saveFileService;
    }

    public PluginTerminalTabPaneService getPluginTerminalTabPaneService() {
        return pluginTerminalTabPaneService;
    }
}
