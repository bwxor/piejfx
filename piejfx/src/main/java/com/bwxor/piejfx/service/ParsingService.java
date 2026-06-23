package com.bwxor.piejfx.service;

import com.bwxor.piejfx.dto.TrailingWhitespaceStatisticsForRemoval;

import java.util.ArrayList;
import java.util.List;

public class ParsingService {
    /**
     * Returns the number of whitespaces on a CodeArea row, starting from the current caret position.
     * @param text
     * @param caretPosition
     * @return the number of whitespaces until the end of the row, or -1 if any non-whitespace character is encountered
     */
    public int countWhitespacesOnCurrentLineAfterCaret(String text, int caretPosition) {
        int noWhitespaces = 0;

        int pos = caretPosition;

        while (pos != text.length() && text.charAt(pos) != '\n') {
            if (text.charAt(pos) != ' ' && text.charAt(pos) != '\t') {
                return -1;
            }

            pos++;
            noWhitespaces++;
        }

        return noWhitespaces;
    }

    public String getSpacesToAppendForNewLineIndentation(String text, int caretPosition) {
        StringBuilder spacesToAppend = new StringBuilder("\n");

        // Find previous row start
        int pos;
        for (pos = caretPosition; pos > 0 && text.charAt(pos - 1) != '\n'; pos--) ;

        // Find how many spaces it has
        while (true) {
            if (pos == text.length() || pos == caretPosition) {
                break;
            }

            if (text.charAt(pos) == '\t' || text.charAt(pos) == ' ') {
                spacesToAppend.append(text.charAt(pos));
                pos++;
            } else {
                break;
            }
        }

        return spacesToAppend.toString();
    }

    public TrailingWhitespaceStatisticsForRemoval getTrailingWhitespaceStatisticsForRemoval(String text, int caretPosition) {
        List<Character> charsEncountered = new ArrayList<>();
        int originalCaretPosition = caretPosition;

        while(caretPosition > 0 && text.charAt(caretPosition-1) != '\n') {
            charsEncountered.add(text.charAt(caretPosition-1));
            caretPosition--;
        }

        charsEncountered = charsEncountered.reversed();

        while(!charsEncountered.isEmpty() && charsEncountered.getFirst() != '\t' && charsEncountered.getFirst() != ' ') {
            charsEncountered.removeFirst();
        }

        int consecutiveSpaces = 0;
        boolean isTab = false;
        int whitespaceStartIndex = caretPosition + (originalCaretPosition - caretPosition - charsEncountered.size());

        for (int i = charsEncountered.size() - 1; i >= 0; i--) {
            char c = charsEncountered.get(i);
            
            if (c == ' ') {
                consecutiveSpaces++;
            }
            else if (c == '\t') {
                isTab = true;
                consecutiveSpaces = 0;
                break;
            }
            else {
                break;
            }
        }

        return new TrailingWhitespaceStatisticsForRemoval(consecutiveSpaces, isTab, whitespaceStartIndex + charsEncountered.size());
    }
}
