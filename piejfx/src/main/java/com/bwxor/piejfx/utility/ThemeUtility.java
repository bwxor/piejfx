package com.bwxor.piejfx.utility;

import com.bwxor.piejfx.state.StageState;
import com.bwxor.piejfx.state.ThemeState;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ThemeUtility {
    private static final String THEME_FOLDER = "themes/";

    public static List<ThemeState.Theme> getThemes() {
        List<ThemeState.Theme> themes = new ArrayList<>();

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ResourceUtility.getResourceByNameAsStream("config/themes.json")))) {
            String themeContent = bufferedReader.readAllAsString();

            JSONObject jsonObject = new JSONObject(themeContent);
            JSONArray arr = jsonObject.getJSONArray("themes");

            for (int i = 0; i < arr.length(); i++) {
                String name = arr.getJSONObject(i).getString("name");
                String file = arr.getJSONObject(i).getString("file");
                themes.add(new ThemeState.Theme(name, ResourceUtility.getResourceByName(THEME_FOLDER + file)));
            }
        } catch (IOException e) {
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
                                    ConfigUtility.rewriteConfig();
                                });
                    }
            );

            menu.getItems().add(menuItem);
        }
    }
}
