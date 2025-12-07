package com.bwxor.piejfx;

import com.bwxor.piejfx.state.StageState;
import com.bwxor.piejfx.state.ThemeState;
import com.bwxor.piejfx.type.RemoveSelectedTabFromPaneResponse;
import com.bwxor.piejfx.utility.ResourceUtility;
import com.bwxor.piejfx.utility.TabPaneUtility;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
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
        scene.getStylesheets().add(ThemeState.instance.getCurrentTheme().getUrl().toExternalForm());
        stage.setTitle("piejfx");
        stage.getIcons().add(new Image(Objects.requireNonNull(ResourceUtility.getResourceByNameAsStream("img/icon.png"))));
        stage.setScene(scene);
        stage.setOnCloseRequest(
                e -> {
                    TabPane tabPane = (TabPane) stage.getScene().getRoot().lookup("#tabPane");

                    int size = tabPane.getTabs().size();
                    tabPane.getSelectionModel().select(size - 1);

                    for (int i = 0; i < size; i++) {
                        var saveResponse = TabPaneUtility.removeSelectedTabFromPane(tabPane);
                        if (saveResponse.equals(RemoveSelectedTabFromPaneResponse.CANCELLED)) {
                            e.consume();
                            return;
                        }
                    }
                }
        );
        stage.show();
    }
}
