package com.bwxor.piejfx.constants;

import net.harawata.appdirs.AppDirsFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

public interface AppDirConstants {
    String APP_NAME = "piejfx";
    String APP_VERSION = "1.0.00";
    String APP_AUTHOR = "Mario-Mihai Mateas";
    String USER_DATA_DIR = AppDirsFactory.getInstance().getUserDataDir(APP_NAME, APP_VERSION, APP_AUTHOR);
    Path CONFIG_DIR = Paths.get(USER_DATA_DIR, "config");
    Path CONFIG_FILE = Paths.get(CONFIG_DIR.toString(), "config.json");
}
