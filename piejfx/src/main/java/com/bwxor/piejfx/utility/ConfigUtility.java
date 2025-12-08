package com.bwxor.piejfx.utility;

import com.bwxor.piejfx.constants.AppDirConstants;
import com.bwxor.piejfx.state.ThemeState;
import net.harawata.appdirs.AppDirsFactory;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class ConfigUtility {
    public static void loadConfig() {
        if (!new File(AppDirConstants.CONFIG_DIR.toUri()).exists()) {
            try (BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(ResourceUtility.getResourceByNameAsStream("internal/indexes"))
            )){
                File file = new File(AppDirConstants.USER_DATA_DIR);
                file.mkdirs();

                String content = bufferedReader.readAllAsString();
                String[] items = content.split("\n");

                for (String s : items ) {
                    s = s.trim();
                    File fileToCreate = new File(Paths.get(AppDirConstants.USER_DATA_DIR, s).toUri());
                    fileToCreate.getParentFile().mkdirs();
                    Files.copy(ResourceUtility.getResourceByNameAsStream(s), Paths.get(AppDirConstants.USER_DATA_DIR.toString(), s));
                }


            } catch (IOException e) {
                // ToDo: Show error
                throw new RuntimeException(e);
            }
            loadConfig();
        } else {
            try (BufferedReader bufferedReader =
                         new BufferedReader(new FileReader(AppDirConstants.CONFIG_FILE.toFile()))) {
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
    }

    public static void rewriteConfig() {
        JSONObject root = new JSONObject();
        JSONObject config = new JSONObject();
        root.put("config", config);

        config.put("currentTheme", ThemeState.instance.getCurrentTheme().getName());

        try (BufferedWriter bufferedWriter =
                     new BufferedWriter(new FileWriter(AppDirConstants.CONFIG_FILE.toFile()))) {

            bufferedWriter.write(root.toString());
            bufferedWriter.flush();
        } catch (IOException e) {
            // ToDo: Show error
            throw new RuntimeException(e);
        }
    }
}
