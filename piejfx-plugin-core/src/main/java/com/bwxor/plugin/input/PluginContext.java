package com.bwxor.plugin.input;

import java.nio.file.Path;

public class PluginContext {
    private ApplicationWindow applicationWindow;
    private ServiceContainer serviceContainer;
    private Path configurationDirectoryPath;

    public PluginContext(ApplicationWindow applicationWindow, ServiceContainer serviceContainer, Path configurationDirectoryPath) {
        this.applicationWindow = applicationWindow;
        this.serviceContainer = serviceContainer;
        this.configurationDirectoryPath = configurationDirectoryPath;
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
}
