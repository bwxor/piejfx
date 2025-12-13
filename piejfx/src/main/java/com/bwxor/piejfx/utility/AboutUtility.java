package com.bwxor.piejfx.utility;

import com.bwxor.piejfx.controller.NotificationYesNoCancelController;
import com.bwxor.piejfx.state.ThemeState;
import com.bwxor.piejfx.type.NotificationYesNoCancelOption;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;

public class AboutUtility {
    public static void showAboutPage() {
        FXMLLoader loader = new FXMLLoader(ResourceUtility.getResourceByName("views/about-view.fxml"));
        Parent root;

        try {
            root = loader.load();
        } catch (IOException e) {
            // ToDo: Show an error here, maybe?
            throw new RuntimeException(e);
        }

        Scene scene = new Scene(root);
        scene.getStylesheets().add(ThemeState.instance.getCurrentTheme().getUrl().toExternalForm());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("About");
        stage.getIcons().add(new Image(Objects.requireNonNull(ResourceUtility.getResourceByNameAsStream("img/icon.png"))));
        stage.setResizable(false);
        stage.initStyle(StageStyle.UTILITY);
        stage.showAndWait();
    }
}
