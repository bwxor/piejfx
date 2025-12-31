package com.bwxor.piejfx.service;

import com.bwxor.piejfx.constants.AppDirConstants;
import com.bwxor.piejfx.controller.NotificationOkController;
import com.bwxor.piejfx.controller.NotificationYesNoCancelController;
import com.bwxor.piejfx.state.ServiceState;
import com.bwxor.piejfx.state.ThemeState;
import com.bwxor.plugin.service.PluginNotificationService;
import com.bwxor.plugin.type.NotificationYesNoCancelOption;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Objects;

public class NotificationService implements PluginNotificationService {
    public void showNotificationOk(String notificationText) {
        ServiceState serviceState = ServiceState.getInstance();

        FXMLLoader loader = new FXMLLoader(serviceState.getResourceService().getResourceByName("views/notification-ok-view.fxml"));
        Parent root;

        try {
            root = loader.load();
        } catch (IOException e) {
            // ToDo: Show an error here, maybe?
            throw new RuntimeException(e);
        }

        NotificationOkController controller = loader.getController();
        controller.setNotificationText(notificationText);
        Scene scene = new Scene(root);
        scene.getStylesheets().add(ThemeState.instance.getCurrentTheme().getUrl().toExternalForm());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Notification");

        stage.getIcons().add(new Image(Objects.requireNonNull(serviceState.getResourceService().getResourceByNameAsStream("img/icons/icon.png"))));
        stage.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(Color.TRANSPARENT);
        try {
            scene.getStylesheets().add(AppDirConstants.DEFAULT_STYLES_FILE.toUri().toURL().toExternalForm());
        } catch (MalformedURLException e) {
            // ToDo: Show an error
            throw new RuntimeException(e);
        }
        stage.showAndWait();
    }

    public NotificationYesNoCancelOption showNotificationYesNoCancel(String notificationText) {
        ServiceState serviceState = ServiceState.getInstance();

        FXMLLoader loader = new FXMLLoader(serviceState.getResourceService().getResourceByName("views/notification-yesnocancel-view.fxml"));
        Parent root;

        try {
            root = loader.load();
        } catch (IOException e) {
            // ToDo: Show an error here, maybe?
            throw new RuntimeException(e);
        }

        NotificationYesNoCancelController controller = loader.getController();
        controller.setNotificationText(notificationText);
        Scene scene = new Scene(root);
        scene.getStylesheets().add(ThemeState.instance.getCurrentTheme().getUrl().toExternalForm());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Notification");
        stage.setOnCloseRequest(e -> {
            controller.setPickedOption(NotificationYesNoCancelOption.CANCEL);
        });
        stage.getIcons().add(new Image(Objects.requireNonNull(serviceState.getResourceService().getResourceByNameAsStream("img/icons/icon.png"))));
        stage.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(Color.TRANSPARENT);
        try {
            scene.getStylesheets().add(AppDirConstants.DEFAULT_STYLES_FILE.toUri().toURL().toExternalForm());
        } catch (MalformedURLException e) {
            // ToDo: Show an error
            throw new RuntimeException(e);
        }
        stage.showAndWait();

        return controller.getPickedOption();
    }
}
