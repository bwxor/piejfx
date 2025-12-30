package com.bwxor.piejfx.service;

import javafx.application.Platform;

public class CloseService {
    public void close() {
        Platform.exit();
        System.exit(0);
    }
}
