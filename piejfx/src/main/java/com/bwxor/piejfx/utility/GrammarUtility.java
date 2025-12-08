package com.bwxor.piejfx.utility;

import com.bwxor.piejfx.constants.AppDirConstants;
import com.bwxor.piejfx.entity.GrammarRule;
import org.fxmisc.richtext.CodeArea;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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
                        JSONObject rulesJsonObject = grammarJsonObject.getJSONObject("rules");

                        for (String key: rulesJsonObject.keySet()) {
                            JSONObject currentRuleJsonObject = rulesJsonObject.getJSONObject(key);

                            GrammarRule grammarRule = new GrammarRule(currentRuleJsonObject.getString("regex"), currentRuleJsonObject.getString("type"));
                            grammarRules.add(grammarRule);
                        }

                        return grammarRules;

                    } catch (IOException e) {
                        // ToDo: Show error
                        throw new RuntimeException();
                    }
                }
            }
        }

        return null;
    }

    public static void highlight(CodeArea codeArea, String fileName) {

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
        } catch (FileNotFoundException e) {
            // ToDo: Show error
            throw new RuntimeException(e);
        } catch (IOException e) {
            // ToDo: Show error
            throw new RuntimeException(e);
        }
    }
}
