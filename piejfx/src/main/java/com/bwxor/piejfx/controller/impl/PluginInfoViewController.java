package com.bwxor.piejfx.controller.impl;

import com.bwxor.piejfx.constants.AppDirConstants;
import com.bwxor.piejfx.controller.MovableViewController;
import com.bwxor.piejfx.dto.FetchedPlugin;
import com.bwxor.piejfx.dto.LoadedPlugin;
import com.bwxor.piejfx.state.HostServicesState;
import com.bwxor.piejfx.state.LoadedPluginsState;
import com.bwxor.piejfx.state.ServiceState;
import com.bwxor.piejfx.type.PluginOperation;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class PluginInfoViewController extends MovableViewController {
    @FXML
    private Label pluginNameLabel;
    @FXML
    private Label pluginAuthorLabel;
    @FXML
    private Label pluginDescriptionLabel;
    @FXML
    private Button pluginOperationButton;
    private FetchedPlugin fetchedPlugin;
    private PluginOperation pluginOperation;

    public void loadPluginInformation(final FetchedPlugin fetchedPlugin) {
        LoadedPluginsState loadedPluginsState = LoadedPluginsState.instance;

        this.fetchedPlugin = fetchedPlugin;

        pluginNameLabel.setText(fetchedPlugin.name());
        pluginAuthorLabel.setText(fetchedPlugin.author());
        pluginDescriptionLabel.setText(fetchedPlugin.description());

        List<LoadedPlugin> loadedPlugins = loadedPluginsState.getPlugins();

        if (loadedPlugins.stream().anyMatch(p -> p.getName().equals(fetchedPlugin.name()))) {
            pluginOperation = PluginOperation.UNINSTALL;
            pluginOperationButton.setText("Uninstall");
        }
        else {
            pluginOperation = PluginOperation.INSTALL;
            pluginOperationButton.setText("Install");
        }
    }

    public void onPluginOperationButtonClick(MouseEvent mouseEvent) {
        if (pluginOperation == PluginOperation.INSTALL) {
            installPlugin();
        } else if (pluginOperation == PluginOperation.UNINSTALL) {
            uninstallPlugin();
        }
    }

    private void installPlugin() {
        pluginOperationButton.setDisable(true);
        pluginOperationButton.setText("Installing...");
        
        CompletableFuture.runAsync(() -> {
            try {
                Path pluginsDir = AppDirConstants.PLUGINS_DIR;
                Files.createDirectories(pluginsDir);
                
                URL url = new URL(fetchedPlugin.url());
                Path tempFile = Files.createTempFile("plugin-", ".zip");
                
                try (InputStream in = url.openStream()) {
                    Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
                }
                
                Path pluginDir = pluginsDir.resolve(fetchedPlugin.name());
                Files.createDirectories(pluginDir);
                
                extractZip(tempFile, pluginDir);
                
                Files.delete(tempFile);
                
                // Update UI on JavaFX thread
                Platform.runLater(() -> {
                    LoadedPluginsState.instance.setPlugins(
                        ServiceState.instance.getPluginService().getPlugins()
                    );
                    
                    pluginOperation = PluginOperation.UNINSTALL;
                    pluginOperationButton.setText("Uninstall");
                    pluginOperationButton.setDisable(false);
                    
                    ServiceState.instance.getNotificationService()
                        .showNotificationOk("Plugin '" + fetchedPlugin.name() + "' installed successfully!");
                });
                
            } catch (IOException e) {
                Platform.runLater(() -> {
                    pluginOperationButton.setText("Install");
                    pluginOperationButton.setDisable(false);
                    ServiceState.instance.getNotificationService()
                        .showNotificationOk("Failed to install plugin: " + e.getMessage());
                });
            }
        });
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

    private void uninstallPlugin() {
        LoadedPluginsState loadedPluginsState = LoadedPluginsState.instance;

        pluginOperationButton.setDisable(true);
        pluginOperationButton.setText("Uninstalling...");

        CompletableFuture.runAsync(() -> {
            try {
                var opt = loadedPluginsState.getPlugins().stream().filter(e -> e.getName().equals(fetchedPlugin.name())).findFirst();
                if (opt.isPresent()) {
                    opt.get().getClassLoader().close();
                }

                Path pluginDir = AppDirConstants.PLUGINS_DIR.resolve(fetchedPlugin.name());
                
                if (Files.exists(pluginDir)) {
                    deleteDirectory(pluginDir);
                    
                    Platform.runLater(() -> {
                        LoadedPluginsState.instance.setPlugins(
                            ServiceState.instance.getPluginService().getPlugins()
                        );
                        
                        pluginOperation = PluginOperation.INSTALL;
                        pluginOperationButton.setText("Install");
                        pluginOperationButton.setDisable(false);
                        
                        ServiceState.instance.getNotificationService()
                            .showNotificationOk("Plugin '" + fetchedPlugin.name() + "' uninstalled successfully!");
                    });
                }
            } catch (IOException e) {
                Platform.runLater(() -> {
                    pluginOperationButton.setText("Uninstall");
                    pluginOperationButton.setDisable(false);
                    ServiceState.instance.getNotificationService()
                        .showNotificationOk("Failed to uninstall plugin: " + e.getMessage());
                });
            }
        });
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

    public void onDownloadPageButtonClick(MouseEvent mouseEvent) {
        HostServicesState hostServicesState = HostServicesState.instance;

        hostServicesState.getHostServices().showDocument(fetchedPlugin.url());
    }
}
