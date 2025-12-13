package com.bwxor.piejfx.provider;

import com.bwxor.piejfx.dto.RGB;
import com.bwxor.piejfx.state.ThemeState;
import com.helger.collection.commons.ICommonsList;
import com.helger.css.decl.CSSDeclaration;
import com.helger.css.decl.CSSStyleRule;
import com.helger.css.decl.CascadingStyleSheet;
import com.helger.css.reader.CSSReader;
import com.techsenger.jeditermfx.core.TerminalColor;
import com.techsenger.jeditermfx.ui.settings.DefaultSettingsProvider;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Optional;

public class ThemeBasedSettingsProvider extends DefaultSettingsProvider {
    private static RGB foregroundColor;
    private static RGB backgroundColor;

    private RGB getColorFromTheme(String color) {
        try {
            CascadingStyleSheet declaration = CSSReader.readFromFile(
                    new File(ThemeState.instance.getCurrentTheme().getUrl().toURI()));

            assert declaration != null;

            ICommonsList<CSSStyleRule> styleRuleList = declaration.getAllStyleRules();

            Optional<CSSStyleRule> styleRule = styleRuleList.stream()
                    .filter(r -> r.getAllSelectors().stream()
                            .anyMatch(s -> s.getAllMembers().stream()
                                    .anyMatch(m -> m.getAsCSSString().equals(".terminal"))))
                    .findFirst();

            if (styleRule.isPresent()) {
                Optional<CSSDeclaration> cssDeclaration = styleRule.get().getAllDeclarations()
                        .stream().filter(d -> d.getProperty().equals(color))
                        .findFirst();

                if (cssDeclaration.isPresent()) {
                    String value = cssDeclaration.get().getExpression().getAllMembers().getFirst().getAsCSSString();
                    return new RGB(
                            Integer.valueOf(value.substring(1, 3), 16),
                            Integer.valueOf(value.substring(3, 5), 16),
                            Integer.valueOf(value.substring(5, 7), 16)
                    );
                }
            }


        } catch (URISyntaxException e) {
            // ToDo: Show an error
            throw new RuntimeException(e);
        }

        return new RGB(0, 0, 0);
    }


    @Override
    public TerminalColor getDefaultForeground() {
        if (foregroundColor == null) {
            foregroundColor = getColorFromTheme("-fx-foreground-color");
        }

        return new TerminalColor(foregroundColor.red(), foregroundColor.green(), foregroundColor.blue());
    }

    @Override
    public TerminalColor getDefaultBackground() {
        if (backgroundColor == null) {
            backgroundColor = getColorFromTheme("-fx-background-color");
        }

        return new TerminalColor(backgroundColor.red(), backgroundColor.green(), backgroundColor.blue());
    }

    public static void resetCache() {
        foregroundColor = null;
        backgroundColor = null;
    }
}