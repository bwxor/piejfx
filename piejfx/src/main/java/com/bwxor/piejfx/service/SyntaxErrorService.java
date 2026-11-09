package com.bwxor.piejfx.service;

import com.bwxor.plugin.service.PluginSyntaxErrorService;
import javafx.application.Platform;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpan;
import org.fxmisc.richtext.model.StyleSpans;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Lets plugins mark ranges of a code area as errors.
 *
 * All changes to the CodeArea are made on the JavaFX application thread. Plugins may
 * call these methods from their own threads, and RichTextFX does not support
 * concurrent style updates, which can leave text rendered invisible until the next
 * layout pass.
 *
 * Only ranges whose style actually changes are touched, so no needless re-render of the
 * whole document is triggered. Existing style classes (e.g. syntax highlighting) are
 * preserved.
 */
public class SyntaxErrorService implements PluginSyntaxErrorService {
    private static final String ERROR_STYLE = "error";

    public void markErrors(CodeArea codeArea, int from, int to) {
        runOnFxThread(() -> restyleRange(codeArea, from, to, true));
    }

    /**
     * Removes the "error" style only from the ranges that currently carry it.
     * Other styles (e.g. syntax highlighting) are left untouched.
     */
    public void clearErrorStyles(CodeArea codeArea) {
        runOnFxThread(() -> restyleRange(codeArea, 0, codeArea.getLength(), false));
    }

    /**
     * Adds (or removes) the "error" style on [from, to), clamped to the document bounds.
     * Must be called on the JavaFX application thread.
     */
    private void restyleRange(CodeArea codeArea, int from, int to, boolean addError) {
        int length = codeArea.getLength();
        int start = Math.max(0, Math.min(from, to));
        int end = Math.min(length, Math.max(from, to));

        if (start >= end) {
            return;
        }

        StyleSpans<Collection<String>> current = codeArea.getStyleSpans(start, end);

        int position = start;
        for (StyleSpan<Collection<String>> span : current) {
            int spanEnd = position + span.getLength();
            Collection<String> styles = span.getStyle();
            boolean hasError = styles != null && styles.contains(ERROR_STYLE);

            // Skip spans that already have the desired state.
            if (hasError != addError) {
                codeArea.setStyle(position, spanEnd, updateStyles(styles, addError));
            }
            position = spanEnd;
        }
    }

    private static Collection<String> updateStyles(Collection<String> styles, boolean addError) {
        Set<String> result = new HashSet<>();
        if (styles != null) {
            result.addAll(styles);
        }

        if (addError) {
            result.add(ERROR_STYLE);
        } else {
            result.remove(ERROR_STYLE);
        }

        return result;
    }

    private static void runOnFxThread(Runnable action) {
        if (Platform.isFxApplicationThread()) {
            action.run();
        } else {
            Platform.runLater(action);
        }
    }
}
