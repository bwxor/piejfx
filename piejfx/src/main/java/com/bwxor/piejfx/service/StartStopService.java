package com.bwxor.piejfx.service;

import com.bwxor.plugin.service.PluginCloseService;
import javafx.application.Platform;

public class StartStopService implements PluginCloseService {
    public void close() {
        Platform.exit();
        System.exit(0);
    }
}
