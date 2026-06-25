package com.bwxor.plugin.input;

import javafx.application.HostServices;

import java.nio.file.Path;

public class PluginContext {
    private ApplicationWindow applicationWindow;
    private ServiceContainer serviceContainer;
    private Path configurationDirectoryPath;
    private HostServices hostServices;
    private Stylesheets stylesheets;

    public PluginContext(ApplicationWindow applicationWindow, ServiceContainer serviceContainer, Path configurationDirectoryPath, HostServices hostServices, Stylesheets stylesheets) {
        this.applicationWindow = applicationWindow;
        this.serviceContainer = serviceContainer;
        this.configurationDirectoryPath = configurationDirectoryPath;
        this.hostServices = hostServices;
        this.stylesheets = stylesheets;
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

    public Stylesheets getStylesheets() {
        return stylesheets;
    }
}
