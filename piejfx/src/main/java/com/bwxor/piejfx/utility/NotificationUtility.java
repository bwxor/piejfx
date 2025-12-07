package com.bwxor.piejfx.utility;

import com.bwxor.piejfx.Application;
import com.bwxor.piejfx.controller.NotificationYesNoCancelController;
import com.bwxor.piejfx.state.StageState;
import com.bwxor.piejfx.type.NotificationYesNoCancelOption;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class NotificationUtility {
    public static NotificationYesNoCancelOption showNotificationYesNoCancel(String notificationText) {
        FXMLLoader loader = new FXMLLoader(ResourceUtility.getResourceByName("notification-yesnocancel-view.fxml"));
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
        scene.getStylesheets().add(ResourceUtility.getResourceByName("themes/light.css").toExternalForm());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Notification");
        stage.getIcons().add(new Image(Objects.requireNonNull(ResourceUtility.getResourceByNameAsStream("img/icon.png"))));
        stage.setResizable(false);
        stage.showAndWait();

        return controller.getPickedOption();
    }
}
