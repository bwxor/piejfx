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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GrammarService {
    private static final short DEBOUNCE_DELAY = 100;

    public Grammar loadGrammar(String extension) {
        ServiceState serviceState = ServiceState.instance;
       
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

    public StyleSpans<Collection<String>> computeHighlighting(
            String text,
            CodeAreaState.IndividualState state) {

        List<GrammarRule> grammarRules = state.getGrammar().getRules();
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

        if (grammarRules == null || text.isEmpty()) {
            spansBuilder.add(Collections.emptyList(), Math.max(text.length(), 1));
            return spansBuilder.create();
        }

        TreeMap<Integer, GrammarMatch> matchMap = new TreeMap<>();

        for (GrammarRule rule : grammarRules) {
            Matcher matcher = rule.getRegexPattern().matcher(text);
            while (matcher.find()) {
                int start = matcher.start();
                int end   = matcher.end();

                Map.Entry<Integer, GrammarMatch> floor = matchMap.floorEntry(start);
                Map.Entry<Integer, GrammarMatch> ceil  = matchMap.ceilingEntry(start);

                boolean overlaps =
                        (floor != null && floor.getValue().getEnd() > start) ||
                                (ceil  != null && ceil.getKey() < end);

                if (!overlaps) {
                    matchMap.put(start, new GrammarMatch(start, end, rule.getType()));
                }
            }
        }

        int lastEnd = 0;
        for (GrammarMatch match : matchMap.values()) {
            if (match.getStart() > lastEnd) {
                spansBuilder.add(Collections.emptyList(), match.getStart() - lastEnd);
            }
            spansBuilder.add(List.of(match.getStyleClass()), match.getEnd() - match.getStart());
            lastEnd = match.getEnd();
        }

        if (lastEnd < text.length()) {
            spansBuilder.add(Collections.emptyList(), text.length() - lastEnd);
        }

        return spansBuilder.create();
    }

    private boolean hasExtension(File file, String extension) {
        ServiceState serviceState = ServiceState.instance;

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

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "highlight-debounce");
            t.setDaemon(true);
            return t;
        });
        individualState.setDebounceScheduler(scheduler);

        codeArea.textProperty().addListener((_, _, newText) -> {
            ScheduledFuture<?> existing = individualState.getPendingHighlight();
            if (existing != null) {
                existing.cancel(false);
            }

            ScheduledFuture<?> future = scheduler.schedule(
                    () -> {
                        StyleSpans<Collection<String>> spans = computeHighlighting(newText, individualState);
                        Platform.runLater(() -> resetCodeAreaStyle(codeArea, spans));
                    },
                    DEBOUNCE_DELAY,
                    TimeUnit.MILLISECONDS
            );

            individualState.setPendingHighlight(future);
        });
    }

    /**
     * Compute the style spans and reset the style of the code area.
     * @param codeArea
     * @param individualState
     */
    public void resetCodeAreaStyle(CodeArea codeArea, CodeAreaState.IndividualState individualState) {
        codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText(), individualState));
    }

    /**
     * Reset style of code area, given the pre-computed style spans.
     * Used for heavy workloads, where another thread needs to pre-compute the styles.
     * @param codeArea
     * @param styleSpans
     */
    public void resetCodeAreaStyle(CodeArea codeArea, StyleSpans<Collection<String>> styleSpans) {
        codeArea.setStyleSpans(0, styleSpans);
    }
}