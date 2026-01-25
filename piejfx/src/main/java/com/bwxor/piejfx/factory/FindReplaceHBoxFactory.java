package com.bwxor.piejfx.factory;

import com.bwxor.piejfx.state.UIState;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.IndexRange;
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

        public static Runnable createReplaceRunnable(TextField findTextField, TextField replaceTextField) {
            UIState uiState = UIState.instance;

            return () -> {
                if (((VBox) uiState.getEditorTabPane().getSelectionModel().getSelectedItem().getContent()).getChildren().getLast() instanceof CodeArea c) {
                    int selectionStart;

                    if (c.getSelectedText().equals(findTextField.getText())) {
                        selectionStart = c.getSelection().getStart();
                        c.replaceText(new IndexRange(selectionStart, c.getSelection().getEnd()), replaceTextField.getText());
                        c.selectRange(selectionStart, selectionStart + replaceTextField.getText().length());
                    } else {
                        selectionStart = c.getText().indexOf(findTextField.getText(), c.getCaretPosition());

                        if (selectionStart != -1) {
                            c.replaceText(new IndexRange(selectionStart, selectionStart + findTextField.getText().length()), replaceTextField.getText());
                            c.selectRange(selectionStart, selectionStart + replaceTextField.getText().length());
                        } else {
                            c.selectRange(0, 0);
                            c.requestFollowCaret();
                            selectionStart = c.getText().indexOf(findTextField.getText(), c.getCaretPosition());
                            if (selectionStart != -1) {
                                c.replaceText(new IndexRange(selectionStart, selectionStart + findTextField.getText().length()), replaceTextField.getText());
                                c.selectRange(selectionStart, selectionStart + replaceTextField.getText().length());
                            }
                        }
                    }

                    c.requestFollowCaret();
                }
            };
        }

        public static Runnable createReplaceAllRunnable(TextField findTextField, TextField replaceTextField) {
            UIState uiState = UIState.instance;

            return () -> {
                if (((VBox) uiState.getEditorTabPane().getSelectionModel().getSelectedItem().getContent()).getChildren().getLast() instanceof CodeArea c) {
                    c.replaceText(0, c.getText().length(), c.getText().replace(findTextField.getText(), replaceTextField.getText()));
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
        replaceTextField.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                EventRunnableFactory.createReplaceRunnable(findTextField, replaceTextField).run();
            }
        });
        hBox.getChildren().add(replaceTextField);

        HBox buttonsHBox = new HBox();
        buttonsHBox.setPadding(new Insets(0, 5, 0, 5));
        buttonsHBox.setSpacing(5);

        Button findButton = new Button("Find");
        findButton.setOnAction(_ -> EventRunnableFactory.createFindRunnable(findTextField).run());

        buttonsHBox.getChildren().add(findButton);

        Button replaceButton = new Button("Replace");
        replaceButton.setOnAction(_ -> EventRunnableFactory.createReplaceRunnable(findTextField, replaceTextField).run());
        buttonsHBox.getChildren().add(replaceButton);

        Button replaceAllButton = new Button("Replace all");
        replaceAllButton.setOnAction(_ -> EventRunnableFactory.createReplaceAllRunnable(findTextField, replaceTextField).run());
        buttonsHBox.getChildren().add(replaceAllButton);

        hBox.getChildren().add(buttonsHBox);

        return hBox;
    }
}
