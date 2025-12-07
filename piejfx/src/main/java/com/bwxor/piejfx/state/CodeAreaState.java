package com.bwxor.piejfx.state;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class CodeAreaState {
    public static class IndividualState {
        private int fontSize = 10;
        private File openedFile;
        private String content;
        private boolean saved;

        public int getFontSize() {
            return fontSize;
        }

        public void setFontSize(int fontSize) {
            this.fontSize = fontSize;
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
    }

    public static CodeAreaState instance = new CodeAreaState();

    private List<IndividualState> individualStates = new ArrayList<>();

    private CodeAreaState() {}

    public List<IndividualState> getIndividualStates() {
        return individualStates;
    }
}
