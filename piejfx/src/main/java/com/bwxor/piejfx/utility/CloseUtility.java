package com.bwxor.piejfx.utility;

import javafx.application.Platform;

public class CloseUtility {
    public static void close() {
        Platform.exit();
        System.exit(0);
    }
}
