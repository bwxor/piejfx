package com.bwxor.piejfx.service;

import com.bwxor.piejfx.constants.AppDirConstants;
import com.bwxor.piejfx.controller.impl.GetPluginsViewController;
import com.bwxor.piejfx.dto.FetchedPlugin;
import com.bwxor.piejfx.exception.FetchPluginsServiceException;
import com.bwxor.piejfx.state.FetchedPluginsState;
import com.bwxor.piejfx.state.ServiceState;
import com.bwxor.piejfx.state.ThemeState;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Objects;

public class GetPluginsViewService {
    public void showGetPluginsView() {
        ServiceState serviceState = ServiceState.instance;

        FXMLLoader loader = new FXMLLoader(serviceState.getResourceService().getResourceByName("views/get-plugins-view.fxml"));
        Parent root;

        try {
            root = loader.load();
        } catch (IOException e) {
            serviceState.getNotificationService().showNotificationOk("Error while trying to load the page.");
            throw new RuntimeException(e);
        }

        GetPluginsViewController controller = loader.getController();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(ThemeState.instance.getCurrentTheme().getUrl().toExternalForm());
        Stage stage = new Stage();

        stage.setScene(scene);
        stage.getIcons().add(new Image(Objects.requireNonNull(serviceState.getResourceService().getResourceByNameAsStream("img/icons/icon.png"))));
        stage.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(Color.TRANSPARENT);
        try {
            scene.getStylesheets().add(AppDirConstants.DEFAULT_STYLES_FILE.toUri().toURL().toExternalForm());
        } catch (MalformedURLException e) {
            serviceState.getNotificationService().showNotificationOk("Error while trying to read the default stylesheet.");
            throw new RuntimeException(e);
        }

        try {
            FetchedPluginsState.instance.setPlugins(serviceState.getFetchPluginsService().fetchPlugins());
            controller.loadVBox();
        } catch (FetchPluginsServiceException e) {
            serviceState.getNotificationService().showNotificationOk("Error while trying to fetch plugins.");
            throw new RuntimeException(e);
        }
        stage.showAndWait();
    }

}
