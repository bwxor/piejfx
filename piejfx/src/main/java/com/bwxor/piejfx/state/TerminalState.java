package com.bwxor.piejfx.state;

import com.bwxor.piejfx.entity.GrammarRule;
import com.techsenger.jeditermfx.ui.JediTermFxWidget;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class TerminalState {
    public static TerminalState instance = new TerminalState();

    private List<JediTermFxWidget> terminals = new ArrayList<>();

    private TerminalState() {}

    public List<JediTermFxWidget> getTerminals() {
        return terminals;
    }
}
