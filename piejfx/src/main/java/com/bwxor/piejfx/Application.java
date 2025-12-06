package com.bwxor.piejfx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("editor-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 750, 500);
        stage.setTitle("piejfx - a JavaFX version of pie");
        stage.getIcons().add(new Image(Objects.requireNonNull(Application.class.getResourceAsStream("img/icon.png"))));
        stage.setScene(scene);
        stage.show();
    }
}
