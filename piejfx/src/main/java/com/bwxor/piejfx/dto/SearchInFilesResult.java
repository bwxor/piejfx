package com.bwxor.piejfx.dto;

public class SearchInFilesResult {
    private final String fileName;
    private final int lineNumber;
    private final String lineText;
    private final String filePath;

    public SearchInFilesResult(String fileName, int lineNumber, String lineText, String filePath) {
        this.fileName = fileName;
        this.lineNumber = lineNumber;
        this.lineText = lineText;
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getLineText() {
        return lineText;
    }

    public String getFilePath() {
        return filePath;
    }
}
