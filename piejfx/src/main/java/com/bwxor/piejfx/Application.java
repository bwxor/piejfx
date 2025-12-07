package com.bwxor.piejfx;

import com.bwxor.piejfx.state.StageState;
import com.bwxor.piejfx.utility.ResourceUtility;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        StageState.instance.setStage(stage);

        FXMLLoader fxmlLoader = new FXMLLoader(ResourceUtility.getResourceByName("editor-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 750, 500);
        stage.setTitle("piejfx");
        stage.getIcons().add(new Image(Objects.requireNonNull(ResourceUtility.getResourceByNameAsStream("img/icon.png"))));
        stage.setScene(scene);
        stage.show();
    }
}
