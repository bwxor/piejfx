package com.bwxor.piejfx.service;

import com.bwxor.piejfx.constants.AppDirConstants;
import com.bwxor.piejfx.dto.FetchedPlugin;
import com.bwxor.piejfx.exception.PluginManagementServiceException;
import com.bwxor.piejfx.state.LoadedPluginsState;
import com.bwxor.piejfx.state.ServiceState;
import com.bwxor.piejfx.state.UIState;
import javafx.application.Platform;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class PluginManagementService {
    public void installPlugin(FetchedPlugin fetchedPlugin) throws PluginManagementServiceException {
        try {
            Path pluginsDir = AppDirConstants.PLUGINS_DIR;
            Files.createDirectories(pluginsDir);

            URL url = new URL(fetchedPlugin.downloadUrl());
            Path tempFile = Files.createTempFile("plugin-", ".zip");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0"
            );

            int code = conn.getResponseCode();
            System.out.println("Response code: " + code);

            System.out.println("URL = " + fetchedPlugin.downloadUrl());

            try (InputStream in = conn.getInputStream()) {
                Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
            }

            extractZip(tempFile, pluginsDir);

            Files.delete(tempFile);

            // Update UI on JavaFX thread
            Platform.runLater(() -> {
                LoadedPluginsState.instance.setPlugins(
                        ServiceState.instance.getPluginService().getPlugins()
                );

                var loadedPlugin = LoadedPluginsState.instance.getPlugins().stream().filter(e -> e.getName().equals(fetchedPlugin.slug())).findFirst();
                loadedPlugin.ifPresent(plugin -> ServiceState.instance.getPluginService().invokeOnLoadIndividually(plugin));
            });

        } catch (IOException e) {
            throw new PluginManagementServiceException(e);
        }
    }

    private void extractZip(Path zipFile, Path destDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path filePath = destDir.resolve(entry.getName());

                if (!filePath.normalize().startsWith(destDir.normalize())) {
                    throw new IOException("Bad zip entry: " + entry.getName());
                }

                if (entry.isDirectory()) {
                    Files.createDirectories(filePath);
                } else {
                    Files.createDirectories(filePath.getParent());
                    Files.copy(zis, filePath, StandardCopyOption.REPLACE_EXISTING);
                }
                zis.closeEntry();
            }
        }
    }

    public void uninstallPlugin(FetchedPlugin fetchedPlugin) throws PluginManagementServiceException {
        LoadedPluginsState loadedPluginsState = LoadedPluginsState.instance;
        UIState uiState = UIState.instance;
        ServiceState serviceState = ServiceState.instance;

        try {
            var opt = loadedPluginsState.getPlugins().stream().filter(e -> e.getName().equals(fetchedPlugin.slug())).findFirst();
            if (opt.isPresent()) {
                opt.get().getClassLoader().close();
            }

            Path pluginDir = AppDirConstants.PLUGINS_DIR.resolve(fetchedPlugin.slug());

            if (Files.exists(pluginDir)) {
                deleteDirectory(pluginDir);

                Platform.runLater(() -> {
                    LoadedPluginsState.instance.setPlugins(
                            ServiceState.instance.getPluginService().getPlugins()
                    );

                    while (uiState.getMenuBar().getMenus().size() > 3) {
                        uiState.getMenuBar().getMenus().removeLast();
                    }

                    while (uiState.getSplitTabPane().getTabs().size() > 1) {
                        uiState.getSplitTabPane().getTabs().removeLast();
                    }

                    serviceState.getPluginService().invokeOnLoad();
                });
            }
        } catch (IOException e) {
            throw new PluginManagementServiceException(e);
        }
    }

    private void deleteDirectory(Path directory) throws IOException {
        if (Files.exists(directory)) {
            Files.walk(directory)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                        }
                    });
        }
    }
}
