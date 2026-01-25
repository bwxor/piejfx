package com.bwxor.piejfx.factory;

import com.bwxor.piejfx.state.UIState;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.fxmisc.richtext.CodeArea;

public class FindReplaceHBoxFactory {
    private static class EventRunnableFactory {
        public static Runnable createFindRunnable(TextField findTextField) {
            UIState uiState = UIState.instance;

            return () -> {
                if (((VBox) uiState.getEditorTabPane().getSelectionModel().getSelectedItem().getContent()).getChildren().getLast() instanceof CodeArea c) {
                    int selectionStart = c.getText().indexOf(findTextField.getText(), c.getCaretPosition());

                    if (selectionStart != -1) {
                        c.selectRange(selectionStart, selectionStart + findTextField.getText().length());
                    } else {
                        c.selectRange(0, 0);
                        c.requestFollowCaret();
                        selectionStart = c.getText().indexOf(findTextField.getText(), c.getCaretPosition());
                        if (selectionStart != -1) {
                            c.selectRange(selectionStart, selectionStart + findTextField.getText().length());
                        }
                    }

                    c.requestFollowCaret();
                }
            };
        }
    }

    public static HBox createFindReplaceHBox() {
        HBox hBox = new HBox();
        hBox.getStyleClass().add("find-replace-box");

        TextField findTextField = new TextField();
        findTextField.setPromptText("Find...");
        findTextField.setOnKeyPressed(
                e -> {
                    if (e.getCode().equals(KeyCode.ENTER)) {
                        EventRunnableFactory.createFindRunnable(findTextField).run();
                    }
                }
        );
        hBox.getChildren().add(findTextField);
        hBox.setPadding(new Insets(5));
        hBox.setSpacing(7);

        TextField replaceTextField = new TextField();
        replaceTextField.setPromptText("Replace...");
        hBox.getChildren().add(replaceTextField);

        HBox buttonsHBox = new HBox();
        buttonsHBox.setPadding(new Insets(0, 5, 0, 5));
        buttonsHBox.setSpacing(5);

        Button findButton = new Button("Find");
        findButton.setOnAction(e -> {
            EventRunnableFactory.createFindRunnable(findTextField).run();
        });

        buttonsHBox.getChildren().add(findButton);

        Button replaceButton = new Button("Replace");
        buttonsHBox.getChildren().add(replaceButton);

        Button replaceAllButton = new Button("Replace all");
        buttonsHBox.getChildren().add(replaceAllButton);

        hBox.getChildren().add(buttonsHBox);

        return hBox;
    }
}
