package com.bwxor.piejfx.factory;

import com.bwxor.piejfx.state.CodeAreaState;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

public class TabFactory {
    public static Tab createEditorTab(String title) {
        CodeArea codeArea = new CodeArea();
        codeArea.setStyle(String.format("-fx-font-size: %dpt", 10));
        CodeAreaState.instance.getIndividualStates().add(new CodeAreaState.IndividualState());

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

        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        return new Tab(title, codeArea);
    }
}
