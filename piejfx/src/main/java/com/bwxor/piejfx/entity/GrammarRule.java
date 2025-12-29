package com.bwxor.piejfx.entity;

import java.util.regex.Pattern;

public class GrammarRule {
    private Pattern regexPattern;
    private String type;

    public GrammarRule(Pattern regexPattern, String type) {
        this.regexPattern = regexPattern;
        this.type = type;
    }

    public Pattern getRegexPattern() {
        return regexPattern;
    }

    public void setRegexPattern(Pattern regexPattern) {
        this.regexPattern = regexPattern;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
