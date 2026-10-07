package com.bwxor.piejfx.controller.impl;

import com.bwxor.piejfx.controller.MovableViewController;
import com.bwxor.piejfx.model.SearchResult;
import com.bwxor.piejfx.state.ServiceState;
import com.bwxor.piejfx.service.SearchInFilesDialogService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class SearchInFilesViewController extends MovableViewController {

    @FXML
    private TextField searchTextField;

    @FXML
    private TableView<SearchResult> resultsTableView;

    @FXML
    private TableColumn<SearchResult, String> fileNameColumn;

    @FXML
    private TableColumn<SearchResult, Integer> lineNumberColumn;

    @FXML
    private TableColumn<SearchResult, String> lineTextColumn;

    @FXML
    private Button closeButton;

    @FXML
    private Button minimizeButton;

    private File workspaceRoot;

    private ObservableList<SearchResult> searchResults;

    @FXML
    public void initialize() {
        searchResults = FXCollections.observableArrayList();
        resultsTableView.setItems(searchResults);
        resultsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        // Set up the table columns
        fileNameColumn.setCellValueFactory(cellData -> cellData.getValue().fileNameProperty());
        lineNumberColumn.setCellValueFactory(cellData -> cellData.getValue().lineNumberProperty().asObject());
        lineTextColumn.setCellValueFactory(cellData -> cellData.getValue().lineTextProperty());

        // Handle row double-click or Enter to open file
        resultsTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                SearchResult selectedResult = resultsTableView.getSelectionModel().getSelectedItem();
                if (selectedResult != null) {
                    openFileAtLine(selectedResult);
                }
            }
        });

        resultsTableView.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                SearchResult selectedResult = resultsTableView.getSelectionModel().getSelectedItem();
                if (selectedResult != null) {
                    openFileAtLine(selectedResult);
                }
            }
        });

        // Enter in search field triggers search; Down arrow moves focus to table
        searchTextField.setOnAction(event -> performSearch());
        searchTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DOWN && !searchResults.isEmpty()) {
                resultsTableView.requestFocus();
                resultsTableView.getSelectionModel().selectFirst();
                event.consume();
            }
        });
    }

    @FXML
    public void onSearchButtonClick() {
        performSearch();
    }

    @FXML
    public void onCloseButtonClick() {
        SearchInFilesDialogService.getInstance().closeDialog();
    }

    @FXML
    public void onMinimizeButtonClick() {
        Stage stage = (Stage) minimizeButton.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    public void onKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ESCAPE) {
            SearchInFilesDialogService.getInstance().closeDialog();
        } else if (keyEvent.isControlDown() && keyEvent.getCode() == KeyCode.W) {
            SearchInFilesDialogService.getInstance().closeDialog();
        }
    }

    public void setWorkspaceRoot(File workspaceRoot) {
        this.workspaceRoot = workspaceRoot;
    }

    public void focusSearchField() {
        searchTextField.requestFocus();
        searchTextField.selectAll();
    }

    private void performSearch() {
        String searchText = searchTextField.getText();
        if (searchText == null || searchText.trim().isEmpty()) {
            return;
        }

        searchResults.clear();

        if (workspaceRoot == null || !workspaceRoot.exists()) {
            return;
        }

        new Thread(() -> {
            List<SearchResult> results = new ArrayList<>();
            try {
                searchInDirectory(workspaceRoot.toPath(), searchText, results);
                Platform.runLater(() -> searchResults.addAll(results));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void searchInDirectory(Path directory, String searchText, List<SearchResult> results) throws IOException {
        try (Stream<Path> paths = Files.walk(directory)) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> !path.toString().contains("\\.git\\") &&
                            !path.toString().contains("\\target\\") &&
                            !path.toString().contains("\\build\\") &&
                            !path.toString().contains("\\node_modules\\"))
                    .forEach(path -> searchInFile(path, searchText, results));
        }
    }

    private void searchInFile(Path filePath, String searchText, List<SearchResult> results) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                if (line.toLowerCase().contains(searchText.toLowerCase())) {
                    SearchResult result = new SearchResult(
                            filePath.toFile().getAbsolutePath(),
                            lineNumber,
                            line.trim()
                    );
                    results.add(result);
                }
                lineNumber++;
            }
        } catch (IOException e) {
            // Skip files that can't be read
        }
    }

    private void openFileAtLine(SearchResult result) {
        File file = new File(result.getFileName());
        if (file.exists()) {
            ServiceState.instance.getFileService().openFile(file);
            // You could add logic here to jump to the specific line number
            // if you have a method in your FileService to do so
        }
    }
}
