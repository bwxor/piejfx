package com.bwxor.piejfx.state;

import com.bwxor.piejfx.service.*;

public class ServiceState {
    private ViewService viewService = new ViewService();
    private CloseService closeService = new CloseService();
    private ConfigurationService configurationService = new ConfigurationService();
    private EditorTabPaneService editorTabPaneService = new EditorTabPaneService();
    private FolderTreeViewService folderTreeViewService = new FolderTreeViewService();
    private GrammarService grammarService = new GrammarService();
    private NotificationService notificationService = new NotificationService();
    private FileService fileService = new FileService();
    private ResizeService resizeService = new ResizeService();
    private ResourceService resourceService = new ResourceService();
    private TerminalTabPaneService terminalTabPaneService = new TerminalTabPaneService();
    private ThemeService themeService = new ThemeService();
    private PluginService pluginService = new PluginService();
    public static final ServiceState instance = new ServiceState();

    private ServiceState() {
    }

    public ViewService getViewService() {
        return viewService;
    }

    public CloseService getCloseService() {
        return closeService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public EditorTabPaneService getEditorTabPaneService() {
        return editorTabPaneService;
    }


    public FolderTreeViewService getFolderTreeViewService() {
        return folderTreeViewService;
    }

    public GrammarService getGrammarService() {
        return grammarService;
    }

    public NotificationService getNotificationService() {
        return notificationService;
    }

    public FileService getFileService() {
        return fileService;
    }

    public ResizeService getResizeService() {
        return resizeService;
    }

    public ResourceService getResourceService() {
        return resourceService;
    }

    public TerminalTabPaneService getTerminalTabPaneService() {
        return terminalTabPaneService;
    }

    public PluginService getPluginService() {
        return pluginService;
    }

    public ThemeService getThemeService() {
        return themeService;
    }
}
