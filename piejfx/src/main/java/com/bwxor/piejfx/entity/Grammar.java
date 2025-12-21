package com.bwxor.piejfx.entity;

import java.util.List;

public class Grammar {
    private List<GrammarRule> rules;
    private List<String> autocompleteWords;

    public List<GrammarRule> getRules() {
        return rules;
    }

    public void setRules(List<GrammarRule> rules) {
        this.rules = rules;
    }

    public List<String> getAutocompleteWords() {
        return autocompleteWords;
    }

    public void setAutocompleteWords(List<String> autocompleteWords) {
        this.autocompleteWords = autocompleteWords;
    }
}
