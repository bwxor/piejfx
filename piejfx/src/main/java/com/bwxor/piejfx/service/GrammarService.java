package com.bwxor.piejfx.service;

import com.bwxor.piejfx.constants.AppDirConstants;
import com.bwxor.piejfx.entity.Grammar;
import com.bwxor.piejfx.entity.GrammarMatch;
import com.bwxor.piejfx.entity.GrammarRule;
import com.bwxor.piejfx.state.CodeAreaState;
import com.bwxor.piejfx.state.ServiceState;
import javafx.application.Platform;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GrammarService {
    private static final short DEBOUNCE_DELAY = 200;

    public Grammar loadGrammar(String extension) {
        ServiceState serviceState = ServiceState.getInstance();
       
        Grammar grammar = new Grammar();

        File grammarDir = new File(AppDirConstants.GRAMMARS_DIR.toUri());

        if (grammarDir.isDirectory()) {
            File[] files = grammarDir.listFiles();

            assert files != null;

            for (var f : files) {
                if (hasExtension(f, extension)) {
                    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(f))) {
                        JSONObject grammarJsonObject = new JSONObject(bufferedReader.readAllAsString());
                        grammar.setRules(loadGrammarRules(grammarJsonObject));
                        grammar.setAutocompleteWords(loadAutocompleteWords(grammarJsonObject));
                    } catch (IOException e) {
                        serviceState.getNotificationService().showNotificationOk("Error while trying to read the grammar file.");
                        throw new RuntimeException();
                    }
                }
            }
        }

        return grammar;
    }

    private List<GrammarRule> loadGrammarRules(JSONObject grammarJsonObject) {
        List<GrammarRule> grammarRules = new ArrayList<>();

        JSONArray rulesJsonArray = grammarJsonObject.getJSONArray("rules");

        for (int i = 0; i < rulesJsonArray.length(); i++) {
            JSONObject currentRuleJsonObject = rulesJsonArray.getJSONObject(i);

            GrammarRule grammarRule = new GrammarRule(Pattern.compile(currentRuleJsonObject.getString("regex"), Pattern.MULTILINE), currentRuleJsonObject.getString("type"));
            grammarRules.add(grammarRule);
        }

        return grammarRules;
    }

    private List<String> loadAutocompleteWords(JSONObject grammarJsonObject) {

        if (grammarJsonObject.has("autocomplete")) {
            List<String> autocompleteWords = new ArrayList<>();

            JSONArray wordsJsonArray = grammarJsonObject.getJSONArray("autocomplete");

            for (int i = 0; i < wordsJsonArray.length(); i++) {
                String currentWord = wordsJsonArray.getString(i);
                autocompleteWords.add(currentWord);
            }

            return autocompleteWords;
        }

        return null;
    }

    public StyleSpans<Collection<String>> computeHighlighting(CodeArea codeArea, CodeAreaState.IndividualState state) {
        List<GrammarRule> grammarRules = state.getGrammar().getRules();

        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

        List<GrammarMatch> matches = new ArrayList<>();

        if (grammarRules != null) {
            for (var rule : grammarRules) {
                Matcher matcher = rule.getRegexPattern().matcher(codeArea.getText());

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

    private boolean hasExtension(File file, String extension) {
        ServiceState serviceState = ServiceState.getInstance();

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
            serviceState.getNotificationService().showNotificationOk("Error while trying to read the grammar file.");
            throw new RuntimeException(e);
        }
    }

    public void setGrammarToCodeArea(CodeArea codeArea, File file) {
        CodeAreaState.IndividualState individualState = CodeAreaState.instance.getIndividualStates().get(Integer.parseInt(codeArea.getId()));

        individualState.setGrammar(loadGrammar(file.getName().substring(file.getName().lastIndexOf(".") + 1)));

        codeArea.textProperty().addListener((obs, oldText, newText) -> {
            Timer existingTimer = individualState.getDebounceTimer();

            if (existingTimer != null) {
                existingTimer.cancel();
            }

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> resetCodeAreaStyle(codeArea, individualState));
                }
            }, DEBOUNCE_DELAY);

            individualState.setDebounceTimer(timer);
        });
    }

    public void resetCodeAreaStyle(CodeArea codeArea, CodeAreaState.IndividualState individualState) {
        codeArea.setStyleSpans(0, computeHighlighting(codeArea, individualState));
    }
}