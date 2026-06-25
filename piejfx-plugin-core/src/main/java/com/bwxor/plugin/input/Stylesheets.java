package com.bwxor.plugin.input;

import java.net.URL;

public class Stylesheets {
    private URL themeURL;
    private URL defaultStylesURL;
    private URL defaultMaximizedURL;

    public Stylesheets(URL themeURL, URL defaultStylesURL, URL defaultMaximizedURL) {
        this.themeURL = themeURL;
        this.defaultStylesURL = defaultStylesURL;
        this.defaultMaximizedURL = defaultMaximizedURL;
    }

    public URL getThemeURL() {
        return themeURL;
    }

    public URL getDefaultStylesURL() {
        return defaultStylesURL;
    }

    public URL getDefaultMaximizedURL() {
        return defaultMaximizedURL;
    }
}
