package com.bwxor.piejfx.state;

import com.bwxor.piejfx.constants.AppDirConstants;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.net.MalformedURLException;

public final class MaximizeState {
    public static final MaximizeState instance = new MaximizeState();

    private boolean maximized;
    private double oldX, oldY, oldWidth, oldHeight;

    private MaximizeState() {
    }

    public boolean isMaximized() {
        return maximized;
    }

    public void toggleMaximize(Stage stage, Button maximizeButton) {
        UIState uiState = UIState.instance;
        ServiceState serviceState = ServiceState.instance;

        if (!maximized) {
            try {
                StageState.instance.getStage().getScene().getStylesheets().remove(AppDirConstants.DEFAULT_STYLES_FILE.toUri().toURL().toExternalForm());
                StageState.instance.getStage().getScene().getStylesheets().add(AppDirConstants.DEFAULT_MAXIMIZED_STYLES_FILE.toUri().toURL().toExternalForm());
            } catch (MalformedURLException e) {
                serviceState.getNotificationService().showNotificationOk("Error while trying to maximize the window.");
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

            maximizeButton.setText("⧉");

            maximized = true;
        } else {
            try {
                StageState.instance.getStage().getScene().getStylesheets().remove(AppDirConstants.DEFAULT_MAXIMIZED_STYLES_FILE.toUri().toURL().toExternalForm());
                StageState.instance.getStage().getScene().getStylesheets().add(AppDirConstants.DEFAULT_STYLES_FILE.toUri().toURL().toExternalForm());
            } catch (MalformedURLException e) {
                serviceState.getNotificationService().showNotificationOk("Error while trying to restore the window from its maximized state.");
            }

            stage.setWidth(oldWidth);
            stage.setHeight(oldHeight);
            stage.setX(oldX);
            stage.setY(oldY);

            maximizeButton.setText("⬜");

            maximized = false;
        }

        if (uiState.getHorizontalSplitPane().getItems().contains(uiState.getSplitTabPane())) {
            uiState.getHorizontalSplitPane().getItems().removeFirst();
            uiState.getHorizontalSplitPane().getItems().addFirst(uiState.getSplitTabPane());
            uiState.getHorizontalSplitPane().setDividerPosition(0, 0.25);
        }
    }
}
