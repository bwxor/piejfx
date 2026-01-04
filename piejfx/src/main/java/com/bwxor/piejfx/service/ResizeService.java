package com.bwxor.piejfx.service;

import com.bwxor.piejfx.state.MaximizeState;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class ResizeService {

    public void addResizeListener(Stage stage) {
        ResizeListener resizeListener = new ResizeListener(stage);
        stage.getScene().addEventHandler(MouseEvent.MOUSE_MOVED, resizeListener);
        stage.getScene().addEventHandler(MouseEvent.MOUSE_PRESSED, resizeListener);
        stage.getScene().addEventHandler(MouseEvent.MOUSE_DRAGGED, resizeListener);
        stage.getScene().addEventHandler(MouseEvent.MOUSE_EXITED, resizeListener);
        stage.getScene().addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, resizeListener);
        ObservableList<Node> children = stage.getScene().getRoot().getChildrenUnmodifiable();
        for (Node child : children) {
            addListenerDeeply(child, resizeListener);
        }
    }

    public void addListenerDeeply(Node node, EventHandler<MouseEvent> listener) {
        node.addEventHandler(MouseEvent.MOUSE_MOVED, listener);
        node.addEventHandler(MouseEvent.MOUSE_PRESSED, listener);
        node.addEventHandler(MouseEvent.MOUSE_DRAGGED, listener);
        node.addEventHandler(MouseEvent.MOUSE_EXITED, listener);
        node.addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, listener);
        if (node instanceof Parent) {
            Parent parent = (Parent) node;
            ObservableList<Node> children = parent.getChildrenUnmodifiable();
            for (Node child : children) {
                addListenerDeeply(child, listener);
            }
        }
    }

    static class ResizeListener implements EventHandler<MouseEvent> {
        private final Stage stage;
        private Cursor cursorEvent = Cursor.DEFAULT;
        private final int border = 8;
        private double startX = 0;
        private double startY = 0;
        private double startStageX = 0;
        private double startStageY = 0;
        private double startStageW = 0;
        private double startStageH = 0;

        public ResizeListener(Stage stage) {
            this.stage = stage;
        }

        @Override
        public void handle(MouseEvent mouseEvent) {
            if (MaximizeState.instance.isMaximized()) return;

            EventType<? extends MouseEvent> mouseEventType = mouseEvent.getEventType();
            Scene scene = stage.getScene();

            double mouseEventX = mouseEvent.getSceneX();
            double mouseEventY = mouseEvent.getSceneY();
            double sceneWidth = scene.getWidth();
            double sceneHeight = scene.getHeight();

            if (MouseEvent.MOUSE_MOVED.equals(mouseEventType)) {
                updateCursor(mouseEventX, mouseEventY, sceneWidth, sceneHeight);
                scene.setCursor(cursorEvent);
            } else if (MouseEvent.MOUSE_EXITED.equals(mouseEventType) || MouseEvent.MOUSE_EXITED_TARGET.equals(mouseEventType)) {
                scene.setCursor(Cursor.DEFAULT);
            } else if (MouseEvent.MOUSE_PRESSED.equals(mouseEventType)) {
                startX = mouseEvent.getScreenX();
                startY = mouseEvent.getScreenY();
                startStageX = stage.getX();
                startStageY = stage.getY();
                startStageW = stage.getWidth();
                startStageH = stage.getHeight();
            } else if (MouseEvent.MOUSE_DRAGGED.equals(mouseEventType)) {
                if (!Cursor.DEFAULT.equals(cursorEvent)) {
                    handleResize(mouseEvent);
                }
            }
        }

        private void updateCursor(double x, double y, double w, double h) {
            if (x < border && y < border) cursorEvent = Cursor.NW_RESIZE;
            else if (x < border && y > h - border) cursorEvent = Cursor.SW_RESIZE;
            else if (x > w - border && y < border) cursorEvent = Cursor.NE_RESIZE;
            else if (x > w - border && y > h - border) cursorEvent = Cursor.SE_RESIZE;
            else if (x < border) cursorEvent = Cursor.W_RESIZE;
            else if (x > w - border) cursorEvent = Cursor.E_RESIZE;
            else if (y < border) cursorEvent = Cursor.N_RESIZE;
            else if (y > h - border) cursorEvent = Cursor.S_RESIZE;
            else cursorEvent = Cursor.DEFAULT;
        }

        private void handleResize(MouseEvent mouseEvent) {
            double mouseScreenX = mouseEvent.getScreenX();
            double mouseScreenY = mouseEvent.getScreenY();

            double minWidth = Math.max(stage.getMinWidth(), border * 2);
            double minHeight = Math.max(stage.getMinHeight(), border * 2);

            if (cursorEvent == Cursor.E_RESIZE || cursorEvent == Cursor.NE_RESIZE || cursorEvent == Cursor.SE_RESIZE) {
                double newWidth = mouseScreenX - stage.getX();
                if (newWidth > minWidth) {
                    stage.setWidth(newWidth);
                }
            }
            else if (cursorEvent == Cursor.W_RESIZE || cursorEvent == Cursor.NW_RESIZE || cursorEvent == Cursor.SW_RESIZE) {
                double fixedRightX = startStageX + startStageW;
                double newWidth = fixedRightX - mouseScreenX;
                if (newWidth > minWidth) {
                    stage.setWidth(newWidth);
                    stage.setX(mouseScreenX);
                }
            }

            if (cursorEvent == Cursor.S_RESIZE || cursorEvent == Cursor.SW_RESIZE || cursorEvent == Cursor.SE_RESIZE) {
                double newHeight = mouseScreenY - stage.getY();
                if (newHeight > minHeight) {
                    stage.setHeight(newHeight);
                }
            }
            else if (cursorEvent == Cursor.N_RESIZE || cursorEvent == Cursor.NW_RESIZE || cursorEvent == Cursor.NE_RESIZE) {
                double fixedBottomY = startStageY + startStageH;
                double newHeight = fixedBottomY - mouseScreenY;
                if (newHeight > minHeight) {
                    stage.setHeight(newHeight);
                    stage.setY(mouseScreenY);
                }
            }

            stage.getScene().getRoot().requestLayout();
        }
    }
}
