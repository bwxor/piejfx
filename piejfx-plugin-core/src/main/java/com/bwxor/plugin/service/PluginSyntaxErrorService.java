package com.bwxor.plugin.service;

import org.fxmisc.richtext.CodeArea;

public interface PluginSyntaxErrorService {
    void markErrors(CodeArea codeArea, int from, int to);
    void clearErrorStyles(CodeArea codeArea);
}
