package com.bwxor.piejfx.entity;

import java.util.Objects;

public class GrammarMatch {
    private int start;
    private int end;
    private String styleClass;

    public GrammarMatch(int start, int end, String styleClass) {
        this.start = start;
        this.end = end;
        this.styleClass = styleClass;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public String getStyleClass() {
        return styleClass;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        GrammarMatch that = (GrammarMatch) o;
        return start <= that.start && that.start < end;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }
}
