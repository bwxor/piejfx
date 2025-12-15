package com.bwxor.piejfx.utility;

import com.bwxor.piejfx.constants.AppDirConstants;
import com.bwxor.piejfx.provider.ThemeBasedSettingsProvider;
import com.bwxor.piejfx.state.MaximizeState;
import com.bwxor.piejfx.state.StageState;
import com.bwxor.piejfx.state.TerminalState;
import com.bwxor.piejfx.state.ThemeState;
import com.techsenger.jeditermfx.ui.JediTermFxWidget;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ThemeUtility {
    public static List<ThemeState.Theme> getThemes() {
        List<ThemeState.Theme> themes = new ArrayList<>();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(AppDirConstants.THEMES_FILE.toFile()))) {
            String themeContent = bufferedReader.readAllAsString();

            JSONObject jsonObject = new JSONObject(themeContent);
            JSONArray arr = jsonObject.getJSONArray("themes");

            for (int i = 0; i < arr.length(); i++) {
                String name = arr.getJSONObject(i).getString("name");
                String file = arr.getJSONObject(i).getString("file");
                themes.add(new ThemeState.Theme(name, Paths.get(String.valueOf(AppDirConstants.THEMES_DIR), file).toUri().toURL()));
            }
        } catch (IOException e) {
            // ToDo: Show an error
            throw new RuntimeException(e);
        }


        return themes;
    }

    public static void loadMenuWithThemes(Menu menu, List<ThemeState.Theme> themes) {
        for (ThemeState.Theme t : themes) {
            MenuItem menuItem = new MenuItem();
            menuItem.setText(t.getName());

            menuItem.setOnAction(
                    e -> {
                        ThemeState.instance.getThemes()
                                .stream()
                                .filter(f -> f.getName().equals(menuItem.getText()))
                                .findAny()
                                .ifPresent(f -> {
                                    ThemeState.instance.setCurrentTheme(f);
                                    StageState.instance.getStage().getScene().getStylesheets().clear();
                                    StageState.instance.getStage().getScene().getStylesheets().add(f.getUrl().toExternalForm());

                                    try {
                                        if (MaximizeState.instance.isMaximized()) {
                                            StageState.instance.getStage().getScene().getStylesheets().add(AppDirConstants.DEFAULT_MAXIMIZED_STYLES_FILE.toUri().toURL().toExternalForm());
                                        } else {
                                            StageState.instance.getStage().getScene().getStylesheets().add(AppDirConstants.DEFAULT_STYLES_FILE.toUri().toURL().toExternalForm());
                                        }
                                    } catch (MalformedURLException ex) {
                                        // ToDo: Show an error
                                        throw new RuntimeException(ex);
                                    }

                                    ConfigUtility.rewriteConfig();

                                    for (JediTermFxWidget terminal : TerminalState.instance.getTerminals()) {
                                        terminal.getTerminalPanel().repaint();
                                    }

                                });

                        ThemeBasedSettingsProvider.resetCache();
                    }
            );

            menu.getItems().add(menuItem);
        }
    }
}
