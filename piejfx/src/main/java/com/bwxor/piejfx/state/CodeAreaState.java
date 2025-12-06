package com.bwxor.piejfx.state;

import java.util.ArrayList;
import java.util.List;

public final class CodeAreaState {
    public static class IndividualState {
        private int fontSize = 10;

        public int getFontSize() {
            return fontSize;
        }

        public void setFontSize(int fontSize) {
            this.fontSize = fontSize;
        }
    }

    public static CodeAreaState instance = new CodeAreaState();

    private List<IndividualState> individualStates = new ArrayList<>();

    private CodeAreaState() {}

    public List<IndividualState> getIndividualStates() {
        return individualStates;
    }
}
