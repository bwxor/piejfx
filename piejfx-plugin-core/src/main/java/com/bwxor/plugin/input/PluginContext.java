package com.bwxor.plugin.input;

import javafx.application.HostServices;

import java.nio.file.Path;

public class PluginContext {
    private ApplicationWindow applicationWindow;
    private ServiceContainer serviceContainer;
    private Path configurationDirectoryPath;
    private HostServices hostServices;

    public PluginContext(ApplicationWindow applicationWindow, ServiceContainer serviceContainer, Path configurationDirectoryPath, HostServices hostServices) {
        this.applicationWindow = applicationWindow;
        this.serviceContainer = serviceContainer;
        this.configurationDirectoryPath = configurationDirectoryPath;
        this.hostServices = hostServices;
    }

    public ApplicationWindow getApplicationWindow() {
        return applicationWindow;
    }

    public ServiceContainer getServiceContainer() {
        return serviceContainer;
    }

    public Path getConfigurationDirectoryPath() {
        return configurationDirectoryPath;
    }

    public HostServices getHostServices() {
        return hostServices;
    }
}
