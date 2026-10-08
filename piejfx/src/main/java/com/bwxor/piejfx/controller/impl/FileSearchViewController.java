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
import java.util.concurrent.atomic.AtomicLong;

public class FileSearchViewController extends MovableViewController {

    private static final int BATCH_SIZE = 50;

    @FXML
    private Label windowTitle;
    @FXML
    private TextField searchTextField;
    @FXML
    private ListView<File> fileListView;

    private File selectedFile;
    private ObservableList<File> matchingFiles;

    // Incremented on every new search and on close; background scans whose
    // generation no longer matches are stale and stop publishing results.
    private final AtomicLong searchGeneration = new AtomicLong();

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

        // Down arrow from the text field moves focus into the list
        searchTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DOWN && !matchingFiles.isEmpty()) {
                fileListView.requestFocus();
                fileListView.getSelectionModel().selectFirst();
                event.consume();
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
        System.out.println("[search for file name] " + searchTextField.getText());
        long generation = searchGeneration.incrementAndGet();
        matchingFiles.clear();
        
        if (searchTerm.isEmpty()) {
            return;
        }
        File rootFolder = FolderTreeViewState.instance.getOpenedFolder();
        if (rootFolder == null) {
            return;
        }

        // Scan the file system off the UI thread; results are published in batches.
        Thread searchThread = new Thread(
                () -> searchFilesInBackground(rootFolder, searchTerm, generation),
                "file-name-search");
        searchThread.setDaemon(true);
        searchThread.start();
    }

    @FXML
    public void onKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            selectedFile = null;
            closeWindow();
        } else if (event.getCode() == KeyCode.ENTER) {
            // Enter from the search field opens the first (or selected) result
            File toOpen = fileListView.getSelectionModel().getSelectedItem();
            if (toOpen == null && !matchingFiles.isEmpty()) {
                toOpen = matchingFiles.get(0);
            }
            if (toOpen != null && toOpen.isFile()) {
                selectedFile = toOpen;
                closeWindow();
            }
        } else if (event.getCode() == KeyCode.DOWN) {
            if (!matchingFiles.isEmpty()) {
                fileListView.requestFocus();
                fileListView.getSelectionModel().selectFirst();
                event.consume();
            }
        }
    }

    private void closeWindow() {
        // Cancel any running background scan
        searchGeneration.incrementAndGet();
        ((Stage) searchTextField.getScene().getWindow()).close();
    }

    private void searchFilesInBackground(File rootFolder, String searchTerm, long generation) {
        List<File> batch = new ArrayList<>();
        searchFilesRecursive(rootFolder, searchTerm, generation, batch);
        publishBatch(batch, generation);
    }

    /**
     * Hands the pending files to the FX thread and clears the batch.
     * Must be called from the background thread only.
     */
    private void publishBatch(List<File> batch, long generation) {
        if (batch.isEmpty()) {
            return;
        }
        List<File> toPublish = new ArrayList<>(batch);
        batch.clear();
        Platform.runLater(() -> {
            // Drop results from scans that were superseded in the meantime
            if (generation == searchGeneration.get()) {
                matchingFiles.addAll(toPublish);
            }
        });
    }

    private boolean isStale(long generation) {
        return generation != searchGeneration.get();
    }

    private void searchFilesRecursive(File directory, String searchTerm, long generation, List<File> batch) {
        if (isStale(generation)) {
            return;
        }
        if (directory == null || !directory.exists() || !directory.isDirectory()) {
            return;
        }

        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (isStale(generation)) {
                return;
            }

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
                    batch.add(file);
                    if (batch.size() >= BATCH_SIZE) {
                        publishBatch(batch, generation);
                    }
                }
            } else if (file.isDirectory()) {
                searchFilesRecursive(file, searchTerm, generation, batch);
            }
        }
    }
}
