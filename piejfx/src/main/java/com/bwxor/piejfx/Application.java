package com.bwxor.piejfx;

import com.bwxor.piejfx.controller.EditorController;
import com.bwxor.piejfx.state.StageState;
import com.bwxor.piejfx.state.ThemeState;
import com.bwxor.piejfx.type.RemoveSelectedTabFromPaneResponse;
import com.bwxor.piejfx.utility.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        Font.loadFont(ResourceUtility.getResourceByName("fonts/FunnelSans-Regular.ttf").toExternalForm(), 10);
        Font.loadFont(ResourceUtility.getResourceByName("fonts/FunnelSans-Bold.ttf").toExternalForm(), 10);
        Font.loadFont(ResourceUtility.getResourceByName("fonts/FunnelSans-Semibold.ttf").toExternalForm(), 10);

        StageState.instance.setStage(stage);

        ConfigUtility.createConfigDirectoryStructure();
        ThemeState.instance.setThemes(ThemeUtility.getThemes());
        ConfigUtility.loadConfig();

        FXMLLoader fxmlLoader = new FXMLLoader(ResourceUtility.getResourceByName("views/editor-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 750, 500);
        EditorController controller = fxmlLoader.getController();
        controller.setParameters(getParameters().getRaw());
        controller.handleParameters();
        scene.getStylesheets().add(ThemeState.instance.getCurrentTheme().getUrl().toExternalForm());
        stage.setTitle("piejfx");
        stage.getIcons().add(new Image(Objects.requireNonNull(ResourceUtility.getResourceByNameAsStream("img/icon.png"))));
        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(Color.TRANSPARENT);
        ResizeUtility.addResizeListener(stage);

        stage.show();
    }
}
