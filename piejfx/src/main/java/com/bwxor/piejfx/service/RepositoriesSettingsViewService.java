package com.bwxor.piejfx.service;

import com.bwxor.piejfx.constants.AppDirConstants;
import com.bwxor.piejfx.controller.impl.AddRepositoryViewController;
import com.bwxor.piejfx.controller.impl.RepositoriesSettingsViewController;
import com.bwxor.piejfx.state.ServiceState;
import com.bwxor.piejfx.state.StageState;
import com.bwxor.piejfx.state.ThemeState;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class RepositoriesSettingsViewService {

    public void showRepositoriesSettingsView() {
        ServiceState serviceState = ServiceState.instance;
        StageState stageState = StageState.instance;

        FXMLLoader loader = new FXMLLoader(serviceState.getResourceService().getResourceByName("views/repositories-settings-view.fxml"));
        Parent root;

        try {
            root = loader.load();
        } catch (IOException e) {
            serviceState.getNotificationService().showNotificationOk("Error while trying to load the page.");
            throw new RuntimeException(e);
        }

        Scene scene = new Scene(root);
        scene.getStylesheets().add(ThemeState.instance.getCurrentTheme().getUrl().toExternalForm());
        Stage stage = new Stage();

        stage.setScene(scene);
        stage.getIcons().add(new Image(Objects.requireNonNull(serviceState.getResourceService().getResourceByNameAsStream("img/icons/icon.png"))));
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(stageState.getManagePluginsStage());
        scene.setFill(Color.TRANSPARENT);
        try {
            scene.getStylesheets().add(AppDirConstants.DEFAULT_STYLES_FILE.toUri().toURL().toExternalForm());
        } catch (MalformedURLException e) {
            serviceState.getNotificationService().showNotificationOk("Error while trying to read the default stylesheet.");
            throw new RuntimeException(e);
        }

        RepositoriesSettingsViewController controller = loader.getController();
        try {
            controller.setRepositories(serviceState.getRepositoriesConfigService().load());
        } catch (IOException e) {
            serviceState.getNotificationService().showNotificationOk("Error while reading repositories.json: " + e.getMessage(), stage);
            controller.setRepositories(List.of());
        }

        stage.showAndWait();
    }

    /**
     * Opens the "Add Repository" window as a modal child of {@code owner} with an empty textbox.
     * Returns the entered URL, or empty if the user cancelled.
     */
    public Optional<String> showAddRepositoryView(Stage owner) {
        return showRepositoryUrlView(owner, "Add Repository", "Add", null);
    }

    /**
     * Opens the "Edit Repository" window as a modal child of {@code owner}, with the textbox
     * pre-filled with {@code currentUrl}. Returns the edited URL, or empty if the user cancelled.
     */
    public Optional<String> showEditRepositoryView(Stage owner, String currentUrl) {
        return showRepositoryUrlView(owner, "Edit Repository", "Update", currentUrl);
    }

    private Optional<String> showRepositoryUrlView(Stage owner, String title, String confirmText, String initialUrl) {
        ServiceState serviceState = ServiceState.instance;

        FXMLLoader loader = new FXMLLoader(serviceState.getResourceService().getResourceByName("views/add-repository-view.fxml"));
        Parent root;

        try {
            root = loader.load();
        } catch (IOException e) {
            serviceState.getNotificationService().showNotificationOk("Error while trying to load the page.", owner);
            throw new RuntimeException(e);
        }

        AddRepositoryViewController controller = loader.getController();
        controller.configure(title, confirmText, initialUrl);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(ThemeState.instance.getCurrentTheme().getUrl().toExternalForm());
        Stage stage = new Stage();

        stage.setScene(scene);
        stage.getIcons().add(new Image(Objects.requireNonNull(serviceState.getResourceService().getResourceByNameAsStream("img/icons/icon.png"))));
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.initOwner(owner);
        scene.setFill(Color.TRANSPARENT);
        try {
            scene.getStylesheets().add(AppDirConstants.DEFAULT_STYLES_FILE.toUri().toURL().toExternalForm());
        } catch (MalformedURLException e) {
            serviceState.getNotificationService().showNotificationOk("Error while trying to read the default stylesheet.", owner);
            throw new RuntimeException(e);
        }

        stage.showAndWait();

        return Optional.ofNullable(controller.getResult());
    }
}
