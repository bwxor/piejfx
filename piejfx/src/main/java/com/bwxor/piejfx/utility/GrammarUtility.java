package com.bwxor.piejfx.utility;

import com.bwxor.piejfx.constants.AppDirConstants;
import com.bwxor.piejfx.entity.GrammarMatch;
import com.bwxor.piejfx.entity.GrammarRule;
import com.bwxor.piejfx.state.CodeAreaState;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GrammarUtility {
    public static List<GrammarRule> loadGrammar(String extension) {
        List<GrammarRule> grammarRules = new ArrayList<>();

        File grammarDir = new File(AppDirConstants.GRAMMARS_DIR.toUri());

        if (grammarDir.isDirectory()) {
            File[] files = grammarDir.listFiles();

            assert files != null;

            for (var f : files) {
                if (hasExtension(f, extension)) {
                    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(f))) {
                        JSONObject grammarJsonObject = new JSONObject(bufferedReader.readAllAsString());
                        JSONArray rulesJsonArray = grammarJsonObject.getJSONArray("rules");

                        for (int i = 0; i<rulesJsonArray.length(); i++) {
                            JSONObject currentRuleJsonObject = rulesJsonArray.getJSONObject(i);

                            GrammarRule grammarRule = new GrammarRule(currentRuleJsonObject.getString("regex"), currentRuleJsonObject.getString("type"));
                            grammarRules.add(grammarRule);
                        }

                        return grammarRules;

                    } catch (IOException e) {
                        NotificationUtility.showNotificationOk("Error while trying to read the grammar file.");
                        throw new RuntimeException();
                    }
                }
            }
        }

        return null;
    }

    public static StyleSpans<Collection<String>> computeHighlighting(CodeArea codeArea, CodeAreaState.IndividualState state) {
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

        List<GrammarMatch> matches = new ArrayList<>();

        if (state.getGrammarRules() != null) {
            for (var rule : state.getGrammarRules()) {
                Pattern pattern = Pattern.compile(rule.getRegexPattern(), Pattern.MULTILINE);
                Matcher matcher = pattern.matcher(codeArea.getText());

                while (matcher.find()) {
                    var grammarMatch = new GrammarMatch(matcher.start(), matcher.end(), rule.getType());
                    if (matches.stream().noneMatch((e -> grammarMatch.getStart() < e.getEnd() && e.getStart() < grammarMatch.getEnd()))) {
                        matches.add(grammarMatch);
                    }
                }
            }
        }

        matches.sort(Comparator.comparingInt(GrammarMatch::getStart));

        int lastEnd = 0;

        for (var match : matches) {
            if (match.getStart() > lastEnd) {
                spansBuilder.add(Collections.emptyList(), match.getStart() - lastEnd);
            }

            int length = match.getEnd() - match.getStart();
            spansBuilder.add(List.of(match.getStyleClass()), length);

            lastEnd = match.getEnd();
        }

        if (lastEnd <= codeArea.getText().length()) {
            spansBuilder.add(Collections.emptyList(), codeArea.getText().length() - lastEnd);
        }

        return spansBuilder.create();
    }

    private static boolean hasExtension(File file, String extension) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            JSONObject jsonObject = new JSONObject(bufferedReader.readAllAsString());
            JSONArray arr = jsonObject.getJSONArray("extensions");

            for (int i = 0; i < arr.length(); i++) {
                if (arr.get(i).equals("." + extension)) {
                    return true;
                }
            }

            return false;
        } catch (IOException e) {
            NotificationUtility.showNotificationOk("Error while trying to read the grammar file.");
            throw new RuntimeException(e);
        }
    }

    public static void setGrammarToCodeArea(CodeArea codeArea, File file) {
        CodeAreaState.IndividualState individualState = CodeAreaState.instance.getIndividualStates().get(Integer.parseInt(codeArea.getId()));

        individualState.setGrammarRules(GrammarUtility.loadGrammar(file.getName().substring(file.getName().lastIndexOf(".") + 1)));

        codeArea.textProperty().addListener((obs, oldText, newText) -> {
            resetCodeAreaStyle(codeArea, individualState);
        });
    }

    public static void resetCodeAreaStyle(CodeArea codeArea, CodeAreaState.IndividualState individualState) {
        codeArea.setStyleSpans(0, GrammarUtility.computeHighlighting(codeArea, individualState));
    }
}
