package com.bwxor.piejfx.state;

import javafx.stage.Stage;

public final class StageState {
    public static StageState instance = new StageState();

    private Stage stage;

    private StageState() {}

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
