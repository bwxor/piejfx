package com.bwxor.piejfx.controller.impl;

import com.bwxor.piejfx.controller.MovableViewController;
import com.bwxor.piejfx.state.FolderTreeViewState;
import com.bwxor.piejfx.state.ServiceState;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileSearchViewController extends MovableViewController {
    @FXML
    private Label windowTitle;
    @FXML
    private TextField searchTextField;
    @FXML
    private ListView<File> fileListView;

    private File selectedFile;
    private ObservableList<File> matchingFiles;

    public void initialize() {
        matchingFiles = FXCollections.observableArrayList();
        fileListView.setItems(matchingFiles);
        
        // Set custom cell factory to display relative paths
        fileListView.setCellFactory(param -> new javafx.scene.control.ListCell<File>() {
            @Override
            protected void updateItem(File file, boolean empty) {
                super.updateItem(file, empty);
                if (empty || file == null) {
                    setText(null);
                } else {
                    File rootFolder = FolderTreeViewState.instance.getOpenedFolder();
                    if (rootFolder != null) {
                        Path rootPath = rootFolder.toPath();
                        Path filePath = file.toPath();
                        String relativePath = rootPath.relativize(filePath).toString();
                        setText(relativePath);
                    } else {
                        setText(file.getName());
                    }
                }
            }
        });
        
        // Handle double-click on list item
        fileListView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                File selected = fileListView.getSelectionModel().getSelectedItem();
                if (selected != null && selected.isFile()) {
                    selectedFile = selected;
                    closeWindow();
                }
            }
        });
        
        // Handle Enter key on list item
        fileListView.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                File selected = fileListView.getSelectionModel().getSelectedItem();
                if (selected != null && selected.isFile()) {
                    selectedFile = selected;
                    closeWindow();
                }
            }
        });
        
        Platform.runLater(() -> searchTextField.requestFocus());
    }

    public File getSelectedFile() {
        return selectedFile;
    }

    @FXML
    public void onCloseButtonClick() {
        selectedFile = null;
        closeWindow();
    }

    @FXML
    public void onMinimizeButtonClick() {
        ((Stage) searchTextField.getScene().getWindow()).setIconified(true);
    }

    @FXML
    public void onSearchTextChanged() {
        String searchTerm = searchTextField.getText().trim().toLowerCase();
        matchingFiles.clear();
        
        if (!searchTerm.isEmpty()) {
            File rootFolder = FolderTreeViewState.instance.getOpenedFolder();
            if (rootFolder != null) {
                List<File> files = searchFiles(rootFolder, searchTerm);
                matchingFiles.addAll(files);
            }
        }
    }

    @FXML
    public void onKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            selectedFile = null;
            closeWindow();
        } else if (event.getCode() == KeyCode.DOWN) {
            if (!matchingFiles.isEmpty()) {
                fileListView.requestFocus();
                fileListView.getSelectionModel().selectFirst();
                event.consume();
            }
        }
    }

    private void closeWindow() {
        ((Stage) searchTextField.getScene().getWindow()).close();
    }

    private List<File> searchFiles(File directory, String searchTerm) {
        List<File> result = new ArrayList<>();
        searchFilesRecursive(directory, searchTerm, result);
        return result;
    }

    private void searchFilesRecursive(File directory, String searchTerm, List<File> result) {
        if (directory == null || !directory.exists() || !directory.isDirectory()) {
            return;
        }

        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            // Skip hidden files and common directories to ignore
            String fileName = file.getName();
            if (fileName.startsWith(".") || 
                fileName.equals("node_modules") || 
                fileName.equals("target") || 
                fileName.equals("build") ||
                fileName.equals("out") ||
                fileName.equals("bin")) {
                continue;
            }

            if (file.isFile()) {
                if (file.getName().toLowerCase().contains(searchTerm)) {
                    result.add(file);
                }
            } else if (file.isDirectory()) {
                searchFilesRecursive(file, searchTerm, result);
            }
        }
    }
}
