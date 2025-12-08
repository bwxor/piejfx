package com.bwxor.piejfx.utility;

import com.bwxor.piejfx.state.ThemeState;
import net.harawata.appdirs.AppDirsFactory;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigUtility {
    private static final String APP_NAME = "piejfx";
    private static final String APP_VERSION = "1.0.00";
    private static final String APP_AUTHOR = "Mario-Mihai Mateas";
    private static final String USER_DATA_DIR = AppDirsFactory.getInstance().getUserDataDir(APP_NAME, APP_AUTHOR, APP_VERSION);
    private static final Path CONFIG_PATH = Paths.get(USER_DATA_DIR, "/config/config.json");

    public static void loadConfig() {
        if (!new File(CONFIG_PATH.toUri()).exists()) {
            try (BufferedReader bufferedReader =
                         new BufferedReader(new InputStreamReader(
                                 ResourceUtility.getResourceByNameAsStream("config/config.json")))) {
                String content = bufferedReader.readAllAsString();
                File file = new File(CONFIG_PATH.toUri());
                file.getParentFile().mkdirs();
                file.createNewFile();
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
                bufferedWriter.write(content);
                bufferedWriter.flush();
                loadConfig();
            } catch (IOException e) {
                // ToDo: Show error
                throw new RuntimeException(e);
            }
        } else {
            try (BufferedReader bufferedReader =
                         new BufferedReader(new FileReader(CONFIG_PATH.toFile()))) {
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
                     new BufferedWriter(new FileWriter(CONFIG_PATH.toFile()))) {

            bufferedWriter.write(root.toString());
            bufferedWriter.flush();
        } catch (IOException e) {
            // ToDo: Show error
            throw new RuntimeException(e);
        }
    }
}
