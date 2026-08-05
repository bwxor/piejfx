package com.bwxor.piejfx.state;

import com.bwxor.piejfx.service.*;

public class ServiceState {
    private ViewService viewService;
    private StartStopService startStopService;
    private ConfigurationService configurationService;
    private EditorTabPaneService editorTabPaneService;
    private FolderTreeViewService folderTreeViewService;
    private GrammarService grammarService;
    private NotificationService notificationService;
    private FileService fileService;
    private ResizeService resizeService;
    private ResourceService resourceService;
    private TerminalTabPaneService terminalTabPaneService;
    private ThemeService themeService;
    private PluginService pluginService;
    private ParsingService parsingService;
    private ManagePluginsViewService managePluginsViewService;
    private FetchPluginsService fetchPluginsService;
    private PluginInfoViewService pluginInfoViewService;
    private PluginManagementService pluginManagementService;
    private PluginEnabledConfigService pluginEnabledConfigService;
    public static final ServiceState instance = new ServiceState();

    private ServiceState() {
    }

    public ViewService getViewService() {
        if (viewService == null) {
            viewService = new ViewService();
        }

        return viewService;
    }

    public StartStopService getStartStopService() {
        if (startStopService == null) {
            startStopService = new StartStopService();
        }

        return startStopService;
    }

    public ConfigurationService getConfigurationService() {
        if (configurationService == null) {
            configurationService = new ConfigurationService();
        }

        return configurationService;
    }

    public EditorTabPaneService getEditorTabPaneService() {
        if (editorTabPaneService == null) {
            editorTabPaneService = new EditorTabPaneService();
        }

        return editorTabPaneService;
    }


    public FolderTreeViewService getFolderTreeViewService() {
        if (folderTreeViewService == null) {
            folderTreeViewService = new FolderTreeViewService();
        }

        return folderTreeViewService;
    }

    public GrammarService getGrammarService() {
        if (grammarService == null) {
            grammarService = new GrammarService();
        }

        return grammarService;
    }

    public NotificationService getNotificationService() {
        if (notificationService == null) {
            notificationService = new NotificationService();
        }

        return notificationService;
    }

    public FileService getFileService() {
        if (fileService == null) {
            fileService = new FileService();
        }

        return fileService;
    }

    public ResizeService getResizeService() {
        if (resizeService == null) {
            resizeService = new ResizeService();
        }

        return resizeService;
    }

    public ResourceService getResourceService() {
        if (resourceService == null) {
            resourceService = new ResourceService();
        }

        return resourceService;
    }

    public TerminalTabPaneService getTerminalTabPaneService() {
        if (terminalTabPaneService == null) {
            terminalTabPaneService = new TerminalTabPaneService();
        }

        return terminalTabPaneService;
    }

    public PluginService getPluginService() {
        if (pluginService == null) {
            pluginService = new PluginService();
        }

        return pluginService;
    }

    public ThemeService getThemeService() {
        if (themeService == null) {
            themeService = new ThemeService();
        }

        return themeService;
    }

    public ParsingService getParsingService() {
        if (parsingService == null) {
            parsingService = new ParsingService();
        }

        return parsingService;
    }

    public ManagePluginsViewService getGetPluginsViewService() {
        if (managePluginsViewService == null) {
            managePluginsViewService = new ManagePluginsViewService();
        }

        return managePluginsViewService;
    }

    public FetchPluginsService getFetchPluginsService() {
        if (fetchPluginsService == null) {
            fetchPluginsService = new FetchPluginsService();
        }

        return fetchPluginsService;
    }

    public PluginInfoViewService getPluginInfoViewService() {
        if (pluginInfoViewService == null) {
            pluginInfoViewService = new PluginInfoViewService();
        }

        return pluginInfoViewService;
    }

    public PluginManagementService getPluginManagementService() {
        if (pluginManagementService == null) {
            pluginManagementService = new PluginManagementService();
        }

        return pluginManagementService;
    }

    public PluginEnabledConfigService getPluginEnabledConfigService() {
        if (pluginEnabledConfigService == null) {
            pluginEnabledConfigService = new PluginEnabledConfigService();
        }

        return pluginEnabledConfigService;
    }
}
