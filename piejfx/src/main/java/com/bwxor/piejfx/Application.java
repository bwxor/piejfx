package com.bwxor.piejfx;

import com.bwxor.piejfx.constants.AppDirConstants;
import com.bwxor.piejfx.controller.EditorController;
import com.bwxor.piejfx.service.*;
import com.bwxor.piejfx.state.HostServicesState;
import com.bwxor.piejfx.state.ServiceState;
import com.bwxor.piejfx.state.StageState;
import com.bwxor.piejfx.state.ThemeState;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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
        ServiceState serviceState = ServiceState.instance;
        HostServicesState.instance.setHostServices(getHostServices());
        
        Font.loadFont(serviceState.getResourceService().getResourceByName("config/fonts/JetBrainsMono-Regular.ttf").toExternalForm(), 10);
        Font.loadFont(serviceState.getResourceService().getResourceByName("config/fonts/SegoeUISymbol.ttf").toExternalForm(), 10);
        Font.loadFont(serviceState.getResourceService().getResourceByName("config/fonts/SegoeUI.ttf").toExternalForm(), 10);
        Font.loadFont(serviceState.getResourceService().getResourceByName("config/fonts/SegoeUIBold.ttf").toExternalForm(), 10);
        StageState.instance.setStage(stage);

        serviceState.getConfigurationService().createConfigDirectoryStructure();
        ThemeState.instance.setThemes(serviceState.getThemeService().getThemes());
        serviceState.getConfigurationService().loadConfig();

        FXMLLoader fxmlLoader = new FXMLLoader(serviceState.getResourceService().getResourceByName("views/editor-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 550);
        EditorController controller = fxmlLoader.getController();
        controller.setParameters(getParameters().getRaw());
        controller.handleParameters();
        scene.getStylesheets().add(AppDirConstants.DEFAULT_STYLES_FILE.toUri().toURL().toExternalForm());
        scene.getStylesheets().add(ThemeState.instance.getCurrentTheme().getUrl().toExternalForm());
        stage.setTitle("piejfx");
        stage.getIcons().add(new Image(Objects.requireNonNull(serviceState.getResourceService().getResourceByNameAsStream("img/icons/icon.png"))));
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> serviceState.getCloseService().close());
        stage.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(Color.TRANSPARENT);
        serviceState.getResizeService().addResizeListener(stage);

        stage.show();
    }
}
