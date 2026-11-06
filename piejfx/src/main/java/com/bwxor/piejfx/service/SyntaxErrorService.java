package com.bwxor.piejfx.service;

import com.bwxor.plugin.service.PluginSyntaxErrorService;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SyntaxErrorService implements PluginSyntaxErrorService {
    public void markErrors(CodeArea codeArea, int from, int to) {
        codeArea.setStyle(from, to, Collections.singleton("error"));
    }

    public void clearErrorStyles(CodeArea codeArea) {
        int length = codeArea.getLength();
        StyleSpans<Collection<String>> spans = codeArea.getStyleSpans(0, length);

        StyleSpans<Collection<String>> cleaned = spans.mapStyles(styles -> {
            Set<String> copy = new HashSet<>(styles);
            copy.remove("error");
            return copy;
        });

        codeArea.setStyleSpans(0, cleaned);
    }
}
