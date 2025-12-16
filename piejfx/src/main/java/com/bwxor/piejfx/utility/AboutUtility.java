package com.bwxor.piejfx.utility;

import com.bwxor.piejfx.constants.AppDirConstants;
import com.bwxor.piejfx.controller.NotificationYesNoCancelController;
import com.bwxor.piejfx.state.StageState;
import com.bwxor.piejfx.state.ThemeState;
import com.bwxor.piejfx.type.NotificationYesNoCancelOption;
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

public class AboutUtility {
    public static void showAboutPage() {
        FXMLLoader loader = new FXMLLoader(ResourceUtility.getResourceByName("views/about-view.fxml"));
        Parent root;

        try {
            root = loader.load();
        } catch (IOException e) {
            NotificationUtility.showNotificationOk("Error while trying to load the About window.");
            throw new RuntimeException(e);
        }

        Scene scene = new Scene(root);
        scene.getStylesheets().add(ThemeState.instance.getCurrentTheme().getUrl().toExternalForm());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("About");
        stage.getIcons().add(new Image(Objects.requireNonNull(ResourceUtility.getResourceByNameAsStream("img/icons/icon.png"))));
        stage.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(Color.TRANSPARENT);
        try {
            scene.getStylesheets().add(AppDirConstants.DEFAULT_STYLES_FILE.toUri().toURL().toExternalForm());
        } catch (MalformedURLException e) {
            NotificationUtility.showNotificationOk("Error while trying to read the default stylesheet.");
            throw new RuntimeException(e);
        }
        stage.showAndWait();
    }
}
