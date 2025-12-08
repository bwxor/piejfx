//package com.bwxor.piejfx.utility;
//
//import com.bwxor.piejfx.constants.AppDirConstants;
//import com.bwxor.piejfx.state.ThemeState;
//import org.fxmisc.richtext.CodeArea;
//import org.json.JSONObject;
//
//import java.io.*;
//
//public class GrammarUtility {
//    public static void loadGrammars() {
//        if (!new File(AppDirConstants.GRAMMARS_PATH.toUri()).exists()) {
//            try (BufferedReader bufferedReader =
//                         new BufferedReader(new InputStreamReader(
//                                 ResourceUtility.getResourceByNameAsStream("config/grammars")))) {
//                String content = bufferedReader.readAllAsString();
//                File file = new File(AppDirConstants.GRAMMARS_PATH.toUri());
//                file.getParentFile().mkdirs();
//                file.createNewFile();
//
//                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
//                bufferedWriter.write(content);
//                bufferedWriter.flush();
//                loadGrammars();
//            } catch (IOException e) {
//                // ToDo: Show error
//                throw new RuntimeException(e);
//            }
//        } else {
//            try (BufferedReader bufferedReader =
//                         new BufferedReader(new FileReader(AppDirConstants.CONFIG_PATH.toFile()))) {
//                String configContent = bufferedReader.readAllAsString();
//
//                JSONObject jsonObject = new JSONObject(configContent);
//                JSONObject obj = jsonObject.getJSONObject("config");
//                String theme = obj.getString("currentTheme");
//
//                ThemeState.instance.getThemes().stream()
//                        .filter(t -> t.getName().equals(theme))
//                        .findFirst()
//                        .ifPresent(t -> ThemeState.instance.setCurrentTheme(t));
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }
//
//    public static void highlight(CodeArea codeArea, String fileName) {
//
//    }
//}
