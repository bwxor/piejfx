package com.bwxor.piejfx.entity;

public class GrammarRule {
    private String regexPattern;
    private String type;

    public GrammarRule(String regexPattern, String type) {
        this.regexPattern = regexPattern;
        this.type = type;
    }

    public String getRegexPattern() {
        return regexPattern;
    }

    public void setRegexPattern(String regexPattern) {
        this.regexPattern = regexPattern;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
