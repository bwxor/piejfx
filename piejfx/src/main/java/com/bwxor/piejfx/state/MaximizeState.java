package com.bwxor.piejfx.state;

import com.bwxor.piejfx.constants.AppDirConstants;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.net.MalformedURLException;

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
            try {
                StageState.instance.getStage().getScene().getStylesheets().remove(AppDirConstants.DEFAULT_STYLES_FILE.toUri().toURL().toExternalForm());
                StageState.instance.getStage().getScene().getStylesheets().add(AppDirConstants.DEFAULT_MAXIMIZED_STYLES_FILE.toUri().toURL().toExternalForm());
            } catch (MalformedURLException e) {
                // ToDo: Show an error
                throw new RuntimeException(e);
            }

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
            try {
                StageState.instance.getStage().getScene().getStylesheets().remove(AppDirConstants.DEFAULT_MAXIMIZED_STYLES_FILE.toUri().toURL().toExternalForm());
                StageState.instance.getStage().getScene().getStylesheets().add(AppDirConstants.DEFAULT_STYLES_FILE.toUri().toURL().toExternalForm());
            } catch (MalformedURLException e) {
                // ToDo: Show an error
                throw new RuntimeException(e);
            }

            stage.setWidth(oldWidth);
            stage.setHeight(oldHeight);
            stage.setX(oldX);
            stage.setY(oldY);

            maximized = false;
        }
    }
}
