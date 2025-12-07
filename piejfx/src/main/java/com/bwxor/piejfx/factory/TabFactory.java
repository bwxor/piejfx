package com.bwxor.piejfx.factory;

import com.bwxor.piejfx.state.CodeAreaState;
import com.bwxor.piejfx.utility.SaveFileUtility;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.ScrollEvent;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

public class TabFactory {
    private static final String EMPTY_STRING = "";
    public static Tab createEditorTab(TabPane tabPane) {
        return createEditorTab(tabPane, EMPTY_STRING);
    }

    public static Tab createEditorTab(TabPane tabPane, String title) {
        Tab tab = new Tab();

        CodeArea codeArea = new CodeArea();
        codeArea.setStyle(String.format("-fx-font-size: %dpt", 10));

        CodeAreaState.IndividualState createdState = new CodeAreaState.IndividualState();
        createdState.setSaved(true);
        CodeAreaState.instance.getIndividualStates().add(createdState);

        codeArea.setOnKeyPressed(e -> {
            if (e.isControlDown()) {
                CodeAreaState.IndividualState individualState = CodeAreaState.instance.getIndividualStates().get(Integer.parseInt(codeArea.getId()));

                if (e.getCode().equals(KeyCode.ADD)) {
                    individualState.setFontSize(individualState.getFontSize() + 2);
                    codeArea.setStyle(String.format("-fx-font-size: %dpt", individualState.getFontSize()));
                }
                else if (e.getCode().equals(KeyCode.SUBTRACT)) {
                    individualState.setFontSize(individualState.getFontSize() - 2);
                    codeArea.setStyle(String.format("-fx-font-size: %dpt", individualState.getFontSize()));
                }
            }
        });

        codeArea.addEventFilter(ScrollEvent.SCROLL,
                e -> {
                    if (e.isControlDown()) {
                        CodeAreaState.IndividualState individualState = CodeAreaState.instance.getIndividualStates().get(Integer.parseInt(codeArea.getId()));

                        if (e.getDeltaY() > 0) {
                            individualState.setFontSize(individualState.getFontSize() + 2);
                            codeArea.setStyle(String.format("-fx-font-size: %dpt", individualState.getFontSize()));
                        }
                        else {
                            individualState.setFontSize(individualState.getFontSize() - 2);
                            codeArea.setStyle(String.format("-fx-font-size: %dpt", individualState.getFontSize()));
                        }
                    }
                }
        );

        codeArea.plainTextChanges().subscribe(
                e -> {
                    CodeAreaState.IndividualState individualState = CodeAreaState.instance.getIndividualStates().get(Integer.parseInt(codeArea.getId()));
                    individualState.setContent(codeArea.getText());

                    if (individualState.isSaved()) {
                        individualState.setSaved(false);
                        tab.setText("*" + tab.getText());
                    }
                }
        );

        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        tab.setText(title);
        tab.setContent(codeArea);
        return tab;
    }
}
