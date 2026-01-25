package com.bwxor.piejfx.service;

import com.bwxor.piejfx.constants.AppDirConstants;
import com.bwxor.piejfx.controller.NewFileController;
import com.bwxor.piejfx.state.*;
import com.bwxor.plugin.dto.NewFileResponse;
import com.bwxor.plugin.service.PluginFileService;
import com.bwxor.plugin.type.NewFileOption;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.fxmisc.richtext.CodeArea;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;

public class FileService implements PluginFileService {
    public void openFile() {
        ServiceState serviceState = ServiceState.instance;

        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(StageState.instance.getStage());

        if (selectedFile != null) {
            openFile(selectedFile);
            serviceState.getPluginService().invokeOnOpenFile(selectedFile);
        }
    }

    public void openFile(File file) {
        ServiceState serviceState = ServiceState.instance;

        UIState uiState = UIState.instance;

        int indexOfOpenedFile = getIndexOfOpenedFileInTabPane(file);

        if (indexOfOpenedFile >= 0) {
            uiState.getEditorTabPane().getSelectionModel().select(indexOfOpenedFile);
        }
        else {
            serviceState.getEditorTabPaneService().addTabToPane(file);
            CodeAreaState.IndividualState state = CodeAreaState.instance.getIndividualStates().get(uiState.getEditorTabPane().getSelectionModel().getSelectedIndex());
            state.setSaved(true);
            uiState.getEditorTabPane().getSelectionModel().getSelectedItem().setText(state.getOpenedFile().getName());
        }

        serviceState.getPluginService().invokeOnOpenFile(file);
    }

    private int getIndexOfOpenedFileInTabPane(File file) {
        UIState uiState = UIState.instance;

        for (int i = 0; i<uiState.getEditorTabPane().getTabs().size(); i++) {
            CodeAreaState.IndividualState state = CodeAreaState.instance.getIndividualStates().get(i);
            if (state.getOpenedFile() != null && state.getOpenedFile().equals(file)) {
                return i;
            }
        }

        return -1;
    }

    public NewFileResponse showNewFileWindow(String title) {
        ServiceState serviceState = ServiceState.instance;

        FXMLLoader loader = new FXMLLoader(serviceState.getResourceService().getResourceByName("views/newfile-view.fxml"));
        Parent root;

        try {
            root = loader.load();

            NewFileController controller = loader.getController();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(ThemeState.instance.getCurrentTheme().getUrl().toExternalForm());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle(title);
            stage.setOnCloseRequest(e -> {
                controller.setNewFileResponse(new NewFileResponse(NewFileOption.CANCEL, null));
            });
            stage.getIcons().add(new Image(Objects.requireNonNull(serviceState.getResourceService().getResourceByNameAsStream("img/icons/icon.png"))));
            stage.initStyle(StageStyle.TRANSPARENT);
            scene.setFill(Color.TRANSPARENT);
            try {
                scene.getStylesheets().add(AppDirConstants.DEFAULT_STYLES_FILE.toUri().toURL().toExternalForm());
            } catch (MalformedURLException e) {
                serviceState.getNotificationService().showNotificationOk("Error while trying to load the default styles.");
                throw new RuntimeException(e);
            }
            controller.setWindowTitle(title);
            stage.showAndWait();

            return controller.getNewFileResponse();

        } catch (IOException e) {
            serviceState.getNotificationService().showNotificationOk("Error while trying to load the window.");
            return null;
        }
    }

    public boolean deleteFolder(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                if (! Files.isSymbolicLink(f.toPath())) {
                    if (!deleteFolder(f)) {
                        return false;
                    }
                }
            }
        }
        return file.delete();
    }

    public void openFolder() {
        ServiceState serviceState = ServiceState.instance;

        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedFile = directoryChooser.showDialog(StageState.instance.getStage());

        if (selectedFile != null) {
            FolderTreeViewState.instance.setOpenedFolder(selectedFile);
            serviceState.getFolderTreeViewService().showFolderTreeView();
        }

        serviceState.getPluginService().invokeOnOpenFolder(selectedFile);
    }

    /**
     * Saves the content of the CodeArea present at the selected tab. If no file is associated with it, a saveFileAs
     * is triggered.
     *
     * @return true if file has been saved successfully and false otherwise
     */
    public boolean saveFile() {
        UIState uiState = UIState.instance;
        ServiceState serviceState = ServiceState.instance;
        CodeAreaState.IndividualState codeAreaState = CodeAreaState.instance.getIndividualStates().get(uiState.getEditorTabPane().getSelectionModel().getSelectedIndex());

        if (codeAreaState.getOpenedFile() == null) {
            return saveFileAs();
        } else {
            try (BufferedWriter br = Files.newBufferedWriter(codeAreaState.getOpenedFile().toPath(), StandardCharsets.UTF_8)) {
                br.write(codeAreaState.getContent());
            } catch (IOException e) {
                serviceState.getNotificationService().showNotificationOk("Error while trying to save the file.");
                throw new RuntimeException(e);
            }

            if (((VBox)uiState.getEditorTabPane().getSelectionModel().getSelectedItem().getContent()).getChildren().getLast() instanceof CodeArea c) {
                serviceState.getGrammarService().setGrammarToCodeArea(c, codeAreaState.getOpenedFile());
                serviceState.getGrammarService().resetCodeAreaStyle(c, codeAreaState);
            }

            codeAreaState.setSaved(true);
            uiState.getEditorTabPane().getSelectionModel().getSelectedItem().setText(codeAreaState.getOpenedFile().getName());
            uiState.getTitleBarLabel().setText(codeAreaState.getOpenedFile().getName());

            serviceState.getFolderTreeViewService().showFolderTreeView();
        }

        serviceState.getPluginService().invokeOnSaveFile(codeAreaState.getOpenedFile());

        return true;
    }

    /**
     * Opens a @ref FileChooser and saves the content of the selected tab into the chosen file.
     *
     * @return true if a file has been chosen and false otherwise
     */
    public boolean saveFileAs() {
        UIState uiState = UIState.instance;
        ServiceState serviceState = ServiceState.instance;

        CodeAreaState.IndividualState codeAreaState = CodeAreaState.instance.getIndividualStates().get(uiState.getEditorTabPane().getSelectionModel().getSelectedIndex());

        String filters;

        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(serviceState.getResourceService().getResourceByNameAsStream("config/extension-filters.json")))) {

            filters = bufferedReader.readAllAsString();

            var fileChooser = new FileChooser();

            JSONObject jsonObject = new JSONObject(filters);
            JSONArray arr = jsonObject.getJSONArray("filters");

            for (int i = 0; i < arr.length(); i++) {
                String description = arr.getJSONObject(i).getString("description");
                String extensions = arr.getJSONObject(i).getString("extensions");
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(description, extensions));
            }

            File selectedFile = fileChooser.showSaveDialog(StageState.instance.getStage());

            if (selectedFile != null) {
                codeAreaState.setOpenedFile(selectedFile);
                return saveFile();
            }

            return false;
        } catch (IOException e) {
            serviceState.getNotificationService().showNotificationOk("Error while trying to save the file.");
            throw new RuntimeException(e);
        }
    }
}
