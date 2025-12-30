package com.bwxor.piejfx.service;

import com.bwxor.piejfx.constants.AppDirConstants;
import com.bwxor.piejfx.controller.NewFileController;
import com.bwxor.piejfx.dto.NewFileResponse;
import com.bwxor.piejfx.state.ServiceState;
import com.bwxor.piejfx.state.ThemeState;
import com.bwxor.piejfx.type.NewFileOption;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.util.Objects;

public class FileOperationsService {
    public NewFileResponse showNewFileWindow(String title) {
        ServiceState serviceState = ServiceState.getInstance();

        FXMLLoader loader = new FXMLLoader(serviceState.getResourceService().getResourceByName("views/newfile-view.fxml"));
        Parent root;

        try {
            root = loader.load();

            NewFileController controller = loader.getController();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(ThemeState.instance.getCurrentTheme().getUrl().toExternalForm());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle(title);
            stage.setOnCloseRequest(e -> {
                controller.setNewFileResponse(new NewFileResponse(NewFileOption.CANCEL, null));
            });
            stage.getIcons().add(new Image(Objects.requireNonNull(serviceState.getResourceService().getResourceByNameAsStream("img/icons/icon.png"))));
            stage.initStyle(StageStyle.TRANSPARENT);
            scene.setFill(Color.TRANSPARENT);
            try {
                scene.getStylesheets().add(AppDirConstants.DEFAULT_STYLES_FILE.toUri().toURL().toExternalForm());
            } catch (MalformedURLException e) {
                serviceState.getNotificationService().showNotificationOk("Error while trying to load the default styles.");
                throw new RuntimeException(e);
            }
            controller.setWindowTitle(title);
            stage.showAndWait();

            return controller.getNewFileResponse();

        } catch (IOException e) {
            serviceState.getNotificationService().showNotificationOk("Error while trying to load the window.");
            return null;
        }
    }

    public boolean deleteFolder(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                if (! Files.isSymbolicLink(f.toPath())) {
                    if (!deleteFolder(f)) {
                        return false;
                    }
                }
            }
        }
        return file.delete();
    }
}
