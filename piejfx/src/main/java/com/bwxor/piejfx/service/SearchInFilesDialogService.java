package com.bwxor.piejfx.service;

import com.bwxor.piejfx.constants.AppDirConstants;
import com.bwxor.piejfx.controller.impl.SearchInFilesViewController;
import com.bwxor.piejfx.state.ServiceState;
import com.bwxor.piejfx.state.ThemeState;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

public class SearchInFilesDialogService {
    private static SearchInFilesDialogService instance;
    private Stage dialogStage;
    private SearchInFilesViewController controller;
    private File workspaceRoot;

    private SearchInFilesDialogService() {
    }

    public static SearchInFilesDialogService getInstance() {
        if (instance == null) {
            instance = new SearchInFilesDialogService();
        }
        return instance;
    }

    public void setWorkspaceRoot(File workspaceRoot) {
        this.workspaceRoot = workspaceRoot;
    }

    public void showSearchDialog(Stage ownerStage) {
        ServiceState serviceState = ServiceState.instance;

        FXMLLoader loader = new FXMLLoader(serviceState.getResourceService().getResourceByName("views/searchinfiles-view.fxml"));
        Parent root;

        try {
            root = loader.load();
        } catch (IOException e) {
            serviceState.getNotificationService().showNotificationOk("Error while trying to load the Search in Files window.");
            throw new RuntimeException(e);
        }

        controller = loader.getController();
        controller.setWorkspaceRoot(workspaceRoot);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(ThemeState.instance.getCurrentTheme().getUrl().toExternalForm());

        try {
            scene.getStylesheets().add(AppDirConstants.DEFAULT_STYLES_FILE.toUri().toURL().toExternalForm());
        } catch (MalformedURLException e) {
            serviceState.getNotificationService().showNotificationOk("Error while trying to read the default stylesheet.");
            throw new RuntimeException(e);
        }

        if (dialogStage == null || !dialogStage.isShowing()) {
            dialogStage = new Stage();
            dialogStage.initStyle(StageStyle.TRANSPARENT);
            dialogStage.initModality(Modality.NONE);
            dialogStage.initOwner(ownerStage);
            dialogStage.setScene(scene);
            dialogStage.setTitle("Search in Files");
            dialogStage.show();
            Platform.runLater(() -> controller.focusSearchField());
        } else {
            dialogStage.toFront();
            controller.setWorkspaceRoot(workspaceRoot);
            Platform.runLater(() -> controller.focusSearchField());
        }
    }

    public void closeDialog() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }
}
