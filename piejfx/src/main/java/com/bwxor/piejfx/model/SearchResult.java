package com.bwxor.piejfx.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SearchResult {
    private final StringProperty fileName;
    private final IntegerProperty lineNumber;
    private final StringProperty lineText;

    public SearchResult(String fileName, int lineNumber, String lineText) {
        this.fileName = new SimpleStringProperty(fileName);
        this.lineNumber = new SimpleIntegerProperty(lineNumber);
        this.lineText = new SimpleStringProperty(lineText);
    }

    public String getFileName() {
        return fileName.get();
    }

    public StringProperty fileNameProperty() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName.set(fileName);
    }

    public int getLineNumber() {
        return lineNumber.get();
    }

    public IntegerProperty lineNumberProperty() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber.set(lineNumber);
    }

    public String getLineText() {
        return lineText.get();
    }

    public StringProperty lineTextProperty() {
        return lineText;
    }

    public void setLineText(String lineText) {
        this.lineText.set(lineText);
    }
}
