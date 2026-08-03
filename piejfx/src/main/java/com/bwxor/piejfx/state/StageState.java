package com.bwxor.piejfx.state;

import javafx.stage.Stage;

public final class StageState {
    public static final StageState instance = new StageState();

    private Stage stage;
    private Stage managePluginsStage;
    private Stage pluginInfoStage;

    private StageState() {}

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Stage getManagePluginsStage() {
        return managePluginsStage;
    }

    public void setManagePluginsStage(Stage managePluginsStage) {
        this.managePluginsStage = managePluginsStage;
    }

    public Stage getPluginInfoStage() {
        return pluginInfoStage;
    }

    public void setPluginInfoStage(Stage pluginInfoStage) {
        this.pluginInfoStage = pluginInfoStage;
    }
}
