package com.bwxor.piejfx.state;

import com.bwxor.piejfx.entity.Grammar;
import javafx.stage.Popup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class CodeAreaState {
    private Popup popup;

    public static class IndividualState {
        private static final String EMPTY_STRING = "";

        private int fontSize = 10;
        private File openedFile;
        private String content;
        private boolean saved;
        private Grammar grammar;

        public int getFontSize() {
            return fontSize;
        }

        public void setFontSize(int fontSize) {
            if (fontSize >= 6 && fontSize <= 30) {
                this.fontSize = fontSize;
            }
        }

        public File getOpenedFile() {
            return openedFile;
        }

        public void setOpenedFile(File openedFile) {
            this.openedFile = openedFile;
        }

        public String getContent() {
            return content == null ? EMPTY_STRING : content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public boolean isSaved() {
            return saved;
        }

        public void setSaved(boolean saved) {
            this.saved = saved;
        }

        public Grammar getGrammar() {
            return grammar;
        }

        public void setGrammar(Grammar grammar) {
            this.grammar = grammar;
        }
    }

    public static CodeAreaState instance = new CodeAreaState();

    private List<IndividualState> individualStates = new ArrayList<>();

    private CodeAreaState() {}

    public List<IndividualState> getIndividualStates() {
        return individualStates;
    }

    public Popup getPopup() {
        return popup;
    }

    public void setPopup(Popup popup) {
        this.popup = popup;
    }
}
