package com.bwxor.piejfx.listener;

import com.bwxor.piejfx.state.CodeAreaState;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.IndexRange;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.stage.Popup;
import org.fxmisc.richtext.CodeArea;

import java.util.ArrayList;

public class AutofillChangeListener implements ChangeListener<String> {
    private static final String EMPTY_STRING = "";
    private static final String SPACE_STRING = " ";

    private boolean openPopup;

    private final CodeArea codeArea;

    public AutofillChangeListener(CodeArea codeArea) {
        this.codeArea = codeArea;
    }

    public boolean isOpenPopup() {
        return openPopup;
    }

    public void setOpenPopup(boolean openPopup) {
        this.openPopup = openPopup;
    }

    @Override
    public void changed(ObservableValue<? extends String> observableValue, String s, String s2) {
        CodeAreaState.IndividualState state = CodeAreaState.instance.getIndividualStates().get(Integer.parseInt(codeArea.getId()));

        if (state.getGrammar() != null && state.getGrammar().getAutocompleteWords() != null) {
            if (openPopup) {
                StringBuilder curr = new StringBuilder();
                if (codeArea.getAnchor() > codeArea.getText().length()) {
                    if (CodeAreaState.instance.getPopup() != null) {
                        CodeAreaState.instance.getPopup().hide();
                    }
                    return;
                }

                for (int i = codeArea.getAnchor(); i >= 0 && i < codeArea.getText().length(); i--) {
                    if (codeArea.getText().charAt(i) == '\n' || codeArea.getText().charAt(i) == ' ' || codeArea.getText().charAt(i) == '\t') {
                        break;
                    } else {
                        curr.append(codeArea.getText().charAt(i));
                    }
                }

                curr = curr.reverse();

                if (curr.toString().trim().equals(EMPTY_STRING)) {
                    if (CodeAreaState.instance.getPopup() != null) {
                        CodeAreaState.instance.getPopup().hide();
                    }

                    return;
                }

                if (!curr.isEmpty()) {
                    ArrayList<String> fil = new ArrayList<>();


                    var autocompleteWords = state.getGrammar().getAutocompleteWords();

                    for (String word : autocompleteWords) {
                        if (word.toLowerCase().startsWith(curr.toString().toLowerCase())) {
                            fil.add(word);
                        }
                    }

                    if (CodeAreaState.instance.getPopup() != null) {
                        CodeAreaState.instance.getPopup().hide();
                    }
                    if (!fil.isEmpty()) {
                        ListView<String> lop = new ListView<>();
                        String finalCurrFinal = curr.toString();
                        lop.setOnMouseClicked(e -> {
                            if (lop.getSelectionModel().getSelectedIndex() >= 0 && e.getClickCount() == 2) {
                                if (CodeAreaState.instance.getPopup() != null) {
                                    codeArea.replaceText(new IndexRange(codeArea.getAnchor() - finalCurrFinal.length(), codeArea.getAnchor()), lop.getSelectionModel().getSelectedItem() + SPACE_STRING);
                                    CodeAreaState.instance.getPopup().hide();
                                }
                            }
                        });
                        lop.setOnKeyPressed(e -> {
                            if (lop.getSelectionModel().getSelectedIndex() >= 0 && e.getCode().equals(KeyCode.ENTER)) {
                                codeArea.replaceText(new IndexRange(codeArea.getAnchor() - finalCurrFinal.length(), codeArea.getAnchor()), lop.getSelectionModel().getSelectedItem() + SPACE_STRING);
                                CodeAreaState.instance.getPopup().hide();
                            } else if (e.getCode().equals(KeyCode.ESCAPE)) {
                                CodeAreaState.instance.getPopup().hide();
                            }
                            else if (e.getCode().equals(KeyCode.UP)) {
                                // Do nothing. AutoComplete box should not hide
                            }
                            else if (e.getCode().equals(KeyCode.DOWN)) {
                                // Do nothing. AutoComplete box should not hide
                            }
                            else {
                                CodeAreaState.instance.getPopup().hide();
                                codeArea.getOnKeyPressed().handle(e);
                            }
                        });
                        for (String string : fil) {
                            lop.getItems().add(string);
                        }

                        CodeAreaState.instance.setPopup(new Popup());

                        lop.setMaxHeight(80);
                        CodeAreaState.instance.getPopup().getContent().addAll(lop);
                        CodeAreaState.instance.getPopup().show(codeArea, codeArea.getCaretBounds().get().getMaxX(), codeArea.getCaretBounds().get().getMaxY());
                        codeArea.requestFocus();

                    }
                    codeArea.requestFocus();
                } else {
                    if (CodeAreaState.instance.getPopup() != null) {
                        CodeAreaState.instance.getPopup().hide();
                    }
                }
            }
            else {
                openPopup = true;
            }
        }
    }
}
