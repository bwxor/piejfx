package com.bwxor.piejfx.state;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

public final class MaximizeState {
    public static MaximizeState instance = new MaximizeState();

    private boolean maximized;
    private double oldX, oldY, oldWidth, oldHeight;

    private MaximizeState() {}

    public boolean isMaximized() {
        return maximized;
    }

    public void toggleMaximize(Stage stage) {
        if (!maximized) {
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();

            oldX = stage.getX();
            oldY = stage.getY();
            oldWidth = stage.getWidth();
            oldHeight = stage.getHeight();

            stage.setX(bounds.getMinX());
            stage.setY(bounds.getMinY());
            stage.setWidth(bounds.getWidth());
            stage.setHeight(bounds.getHeight());

            maximized = true;
        }
        else {
            stage.setWidth(oldWidth);
            stage.setHeight(oldHeight);
            stage.setX(oldX);
            stage.setY(oldY);

            maximized = false;
        }
    }
}
