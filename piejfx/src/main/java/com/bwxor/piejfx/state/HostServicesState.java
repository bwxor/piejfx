package com.bwxor.piejfx.state;

import javafx.application.HostServices;

public class HostServicesState {
    private HostServices hostServices;
    public static final HostServicesState instance = new HostServicesState();

    private HostServicesState() {
    }

    public HostServices getHostServices() {
        return hostServices;
    }

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }
}
