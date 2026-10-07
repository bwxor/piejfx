package com.bwxor.piejfx.service;

import com.bwxor.piejfx.controller.impl.SearchInFilesViewController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;

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
        try {
            if (dialogStage == null || !dialogStage.isShowing()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bwxor/piejfx/views/searchinfiles-view.fxml"));
                Parent root = loader.load();
                controller = loader.getController();
                controller.setWorkspaceRoot(workspaceRoot);

                dialogStage = new Stage();
                dialogStage.initStyle(StageStyle.TRANSPARENT);
                dialogStage.initModality(Modality.NONE);
                dialogStage.initOwner(ownerStage);

                Scene scene = new Scene(root);
                scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
                scene.getStylesheets().add(getClass().getResource("/com/bwxor/piejfx/themes/dark.css").toExternalForm());

                dialogStage.setScene(scene);
                dialogStage.setTitle("Search in Files");

                dialogStage.show();

                Platform.runLater(() -> controller.focusSearchField());
            } else {
                dialogStage.toFront();
                controller.setWorkspaceRoot(workspaceRoot);
                Platform.runLater(() -> controller.focusSearchField());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeDialog() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }
}
