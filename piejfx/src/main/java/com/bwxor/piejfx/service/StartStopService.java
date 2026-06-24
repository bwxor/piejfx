package com.bwxor.piejfx.service;

import com.bwxor.piejfx.Application;
import com.bwxor.plugin.service.PluginCloseService;
import javafx.application.Platform;

import java.io.File;

public class StartStopService implements PluginCloseService {
    public void close() {
        Platform.exit();
        System.exit(0);
    }

    public void restart() {
        try {
            String java = System.getProperty("java.home")
                    + "/bin/java";

            String jar = new File(
                    Application.class
                            .getProtectionDomain()
                            .getCodeSource()
                            .getLocation()
                            .toURI()
            ).getPath();

            new ProcessBuilder(
                    java,
                    "-jar",
                    jar
            ).start();

            Platform.exit();
            System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
