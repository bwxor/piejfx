package com.bwxor.piejfx.constants;

import net.harawata.appdirs.AppDirsFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Constants referencing paths of directories stored under the user's appdata (e.g. on Windows APP_DATA/Local) folder.
 */
public interface AppDirConstants {
    String APP_NAME = "piejfx";
    String APP_VERSION = "1.0.00";
    String APP_AUTHOR = "bwxor";
    String USER_DATA_DIR = AppDirsFactory.getInstance().getUserDataDir(APP_NAME, APP_VERSION, APP_AUTHOR);
    Path CONFIG_DIR = Paths.get(USER_DATA_DIR, "config");
    Path PLUGINS_DIR = Paths.get(USER_DATA_DIR, "plugins");
    Path GRAMMARS_DIR = Paths.get(CONFIG_DIR.toString(), "grammars");
    Path THEMES_DIR = Paths.get(CONFIG_DIR.toString(), "themes");
    Path STYLES_DIR = Paths.get(CONFIG_DIR.toString(), "styles");
    Path CONFIG_FILE = Paths.get(CONFIG_DIR.toString(), "config.json");
    Path THEMES_FILE = Paths.get(CONFIG_DIR.toString(), "themes.json");
    Path DEFAULT_STYLES_FILE = Paths.get(STYLES_DIR.toString(), "default.css");
    Path DEFAULT_MAXIMIZED_STYLES_FILE = Paths.get(STYLES_DIR.toString(), "default-maximized.css");
}
