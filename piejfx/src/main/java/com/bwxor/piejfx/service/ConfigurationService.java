package com.bwxor.piejfx.service;

import com.bwxor.piejfx.constants.AppDirConstants;
import com.bwxor.piejfx.state.ServiceState;
import com.bwxor.piejfx.state.ThemeState;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigurationService {
    public void createConfigDirectoryStructure() {
        if (!new File(AppDirConstants.CONFIG_DIR.toUri()).exists()) {
            copyResourceFilesIntoAppdata();
        }
    }

    public void loadConfig() {
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

    public void rewriteConfig() {
        ServiceState serviceState = ServiceState.instance;

        JSONObject root = new JSONObject();
        JSONObject config = new JSONObject();
        root.put("config", config);

        config.put("currentTheme", ThemeState.instance.getCurrentTheme().getName());

        try (BufferedWriter bufferedWriter =
                     new BufferedWriter(new FileWriter(AppDirConstants.CONFIG_FILE.toFile()))) {

            bufferedWriter.write(root.toString());
            bufferedWriter.flush();
        } catch (IOException e) {
            serviceState.getNotificationService().showNotificationOk("Error while trying to rewrite the configuration.");
            throw new RuntimeException(e);
        }
    }

    private void copyResourceFilesIntoAppdata() {
        ServiceState serviceState = ServiceState.instance;

        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(serviceState.getResourceService().getResourceByNameAsStream("internal/indexes"))
        )){
            File file = new File(AppDirConstants.USER_DATA_DIR);
            file.mkdirs();

            String content = bufferedReader.readAllAsString();
            String[] items = content.split("\n");

            for (String s : items ) {
                s = s.trim();
                Path target = Paths.get(AppDirConstants.USER_DATA_DIR, s);
                File fileToCreate = new File(target.toUri());
                fileToCreate.getParentFile().mkdirs();
                Files.copy(serviceState.getResourceService().getResourceByNameAsStream(s), target);
            }
        } catch (IOException e) {
            serviceState.getNotificationService().showNotificationOk("Error while trying to write the config folder to the local disk.");
            throw new RuntimeException(e);
        }
    }
}
