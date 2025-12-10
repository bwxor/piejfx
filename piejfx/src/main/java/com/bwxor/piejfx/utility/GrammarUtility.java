package com.bwxor.piejfx.utility;

import com.bwxor.piejfx.constants.AppDirConstants;
import com.bwxor.piejfx.state.ThemeState;
import org.fxmisc.richtext.CodeArea;
import org.json.JSONObject;

import java.io.*;

public class GrammarUtility {
    public static void loadGrammars() {
        try (BufferedReader bufferedReader =
                     new BufferedReader(new FileReader(AppDirConstants.GRAMMAR_DIR.toFile()))) {
            String configContent = bufferedReader.readAllAsString();

            JSONObject jsonObject = new JSONObject(configContent);
            JSONObject obj = jsonObject.getJSONObject("config");
            String theme = obj.getString("currentTheme");

            ThemeState.instance.getThemes().stream()
                    .filter(t -> t.getName().equals(theme))
                    .findFirst()
                    .ifPresent(t -> ThemeState.instance.setCurrentTheme(t));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void highlight(CodeArea codeArea, String fileName) {

    }
}
