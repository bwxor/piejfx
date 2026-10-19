package com.bwxor.piejfx.state;

public final class WordWrapState {
    public static final WordWrapState instance = new WordWrapState();

    private boolean wordWrapEnabled = false;

    private WordWrapState() {}

    public boolean isWordWrapEnabled() {
        return wordWrapEnabled;
    }

    public void setWordWrapEnabled(boolean wordWrapEnabled) {
        this.wordWrapEnabled = wordWrapEnabled;
    }
}
