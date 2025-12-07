package com.bwxor.piejfx.utility;

import com.bwxor.piejfx.state.StageState;
import com.bwxor.piejfx.state.ThemeState;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ThemeUtility {
    private static final String THEME_FOLDER_NAME = "themes/";

    public static List<ThemeState.Theme> getThemes() {
        List<ThemeState.Theme> themes = new ArrayList<>();

        try {
            for (final File fileEntry : Objects.requireNonNull(new File(ResourceUtility.getResourceByName(THEME_FOLDER_NAME).toURI()).listFiles())) {
                ThemeState.Theme theme = new ThemeState.Theme(fileEntry.getName(),
                        ResourceUtility.getResourceByName(THEME_FOLDER_NAME + fileEntry.getName()));
                themes.add(theme);
            }
        } catch (URISyntaxException e) {
            // ToDo: Show error
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
                                });
                    }
            );

            menu.getItems().add(menuItem);
        }
    }
}
