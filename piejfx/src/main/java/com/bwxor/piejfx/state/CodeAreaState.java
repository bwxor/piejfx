package com.bwxor.piejfx.state;

import com.bwxor.piejfx.entity.GrammarRule;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class CodeAreaState {
    public static class IndividualState {
        private int fontSize = 10;
        private File openedFile;
        private String content;
        private boolean saved;
        private List<GrammarRule> grammarRules;

        public int getFontSize() {
            return fontSize;
        }

        public void setFontSize(int fontSize) {
            if (fontSize >= 10 && fontSize <= 30) {
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
            return content;
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

        public List<GrammarRule> getGrammarRules() {
            return grammarRules;
        }

        public void setGrammarRules(List<GrammarRule> grammarRules) {
            this.grammarRules = grammarRules;
        }
    }

    public static CodeAreaState instance = new CodeAreaState();

    private List<IndividualState> individualStates = new ArrayList<>();

    private CodeAreaState() {}

    public List<IndividualState> getIndividualStates() {
        return individualStates;
    }
}
