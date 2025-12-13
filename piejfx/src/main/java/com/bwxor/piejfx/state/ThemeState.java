package com.bwxor.piejfx.state;

import com.bwxor.piejfx.utility.ResourceUtility;

import java.net.URL;
import java.util.List;

public final class ThemeState {
    public static class Theme {
        private String name;
        private URL url;

        public Theme(String name, URL url) {
            this.name = name;
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public URL getUrl() {
            return url;
        }
    }

    public static ThemeState instance = new ThemeState();

    private Theme currentTheme = new Theme("light.css", ResourceUtility.getResourceByName("config/themes/light.css"));
    private List<Theme> themes;

    private ThemeState() {}

    public Theme getCurrentTheme() {
        return currentTheme;
    }

    public void setCurrentTheme(Theme currentTheme) {
        this.currentTheme = currentTheme;
    }

    public List<Theme> getThemes() {
        return themes;
    }

    public void setThemes(List<Theme> themes) {
        this.themes = themes;
    }
}
