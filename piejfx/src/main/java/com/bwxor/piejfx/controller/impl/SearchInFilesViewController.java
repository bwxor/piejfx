package com.bwxor.piejfx.controller.impl;

import com.bwxor.piejfx.controller.MovableViewController;
import com.bwxor.piejfx.model.SearchResult;
import com.bwxor.piejfx.state.ServiceState;
import com.bwxor.piejfx.service.SearchInFilesDialogService;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SearchInFilesViewController extends MovableViewController {

    private static final int BATCH_SIZE = 100;

    // Upper bound for the text shown in the results table for a single match
    private static final int MAX_DISPLAY_LENGTH = 300;

    // Stop after this many matches so a common search term can't flood the table
    private static final int MAX_RESULTS = 5000;

    // Files larger than this are skipped (minified bundles, dumps, logs...)
    private static final long MAX_FILE_SIZE = 5L * 1024 * 1024;

    // How many leading bytes are checked for NUL bytes to detect binary files
    private static final int BINARY_SNIFF_BYTES = 8192;

    private static final Set<String> SKIPPED_DIRS =
            Set.of(".git", "target", "build", "node_modules", ".idea", "out", ".gradle");

    private static final Set<String> TEXT_EXTENSIONS = Set.of(
            "java", "txt", "xml", "json", "properties", "yml", "yaml", "md", "js",
            "ts", "html", "css", "py", "c", "cpp", "h", "hpp", "cs", "go", "rs",
            "kt", "swift", "rb", "php", "sh", "bat", "fxml", "gradle", "kts",
            "sql", "csv", "ini", "cfg", "toml", "jsx", "tsx", "scss", "less"
    );

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

    // Incremented on every new search; background scans whose generation no
    // longer matches are stale and stop publishing results.
    private final AtomicLong searchGeneration = new AtomicLong();

    @FXML
    public void initialize() {
        searchResults = FXCollections.observableArrayList();
        resultsTableView.setItems(searchResults);
        resultsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        // The model keeps the real path (needed to open the file); only the
        // displayed text is sanitized, because file names can contain odd characters too.
        fileNameColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(sanitizeForDisplay(cellData.getValue().getFileName())));
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
    public void onSearchTextChanged() {
        performSearch();
    }

    @FXML
    public void onCloseButtonClick() {
        cancelSearch();
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
            cancelSearch();
            SearchInFilesDialogService.getInstance().closeDialog();
        } else if (keyEvent.isControlDown() && keyEvent.getCode() == KeyCode.W) {
            cancelSearch();
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

    private void cancelSearch() {
        searchGeneration.incrementAndGet();
    }

    private void performSearch() {
        String searchText = searchTextField.getText();
        // Any scan still running belongs to the previous query: cancel it
        long generation = searchGeneration.incrementAndGet();

        // Clear before the early returns so an emptied query also empties the table
        searchResults.clear();

        if (searchText == null || searchText.trim().isEmpty()) {
            return;
        }
        if (workspaceRoot == null || !workspaceRoot.exists()) {
            return;
        }

        // Scan the files off the UI thread; results are published in batches.
        Thread searchThread = new Thread(
                () -> searchInDirectory(workspaceRoot.toPath(), searchText, generation),
                "search-in-files");
        searchThread.setDaemon(true);
        searchThread.start();
    }

    private void searchInDirectory(Path directory, String searchText, long generation) {
        List<SearchResult> batch = new ArrayList<>();
        AtomicInteger totalFound = new AtomicInteger();
        String lowerSearchText = searchText.toLowerCase(Locale.ROOT);
        try {
            Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    if (isStale(generation)) {
                        return FileVisitResult.TERMINATE;
                    }
                    Path name = dir.getFileName();
                    return name != null && SKIPPED_DIRS.contains(name.toString())
                            ? FileVisitResult.SKIP_SUBTREE
                            : FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
                    if (isStale(generation) || totalFound.get() >= MAX_RESULTS) {
                        return FileVisitResult.TERMINATE;
                    }
                    if (attrs.isRegularFile()
                            && attrs.size() <= MAX_FILE_SIZE
                            && hasTextExtension(path)
                            && !looksBinary(path)) {
                        searchInFile(path, lowerSearchText, batch, totalFound, generation);
                        if (batch.size() >= BATCH_SIZE) {
                            publishBatch(batch, generation);
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    // Access denied or otherwise unreadable: skip silently
                    return isStale(generation) ? FileVisitResult.TERMINATE : FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            // Ignore: the walk failed for a reason that is not worth reporting to the user
        }
        publishBatch(batch, generation);
    }

    private static boolean hasTextExtension(Path path) {
        String name = path.getFileName().toString().toLowerCase(Locale.ROOT);
        int dot = name.lastIndexOf('.');
        return dot >= 0 && TEXT_EXTENSIONS.contains(name.substring(dot + 1));
    }

    /**
     * A file with a NUL byte near the start is treated as binary. Decoding binary
     * data as text produces random characters from many scripts, which is what
     * pushes JavaFX's DirectWrite text shaping into its crashing code path.
     */
    private static boolean looksBinary(Path path) {
        try (InputStream in = Files.newInputStream(path)) {
            byte[] buf = in.readNBytes(BINARY_SNIFF_BYTES);
            for (byte b : buf) {
                if (b == 0) {
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            return true; // unreadable: skip it
        }
    }

    /**
     * Hands the pending results to the FX thread and clears the batch.
     * Must be called from the background thread only.
     */
    private void publishBatch(List<SearchResult> batch, long generation) {
        if (batch.isEmpty()) {
            return;
        }
        List<SearchResult> toPublish = new ArrayList<>(batch);
        batch.clear();
        Platform.runLater(() -> {
            // Drop results from scans that were superseded in the meantime
            if (generation == searchGeneration.get()) {
                searchResults.addAll(toPublish);
            }
        });
    }

    private boolean isStale(long generation) {
        return generation != searchGeneration.get();
    }

    private void searchInFile(Path filePath, String lowerSearchText, List<SearchResult> results,
                              AtomicInteger totalFound, long generation) {
        CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder()
                .onMalformedInput(CodingErrorAction.REPLACE)
                .onUnmappableCharacter(CodingErrorAction.REPLACE);
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Files.newInputStream(filePath), decoder))) {
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                if (isStale(generation) || totalFound.get() >= MAX_RESULTS) {
                    return;
                }
                // Match against the raw line, but only ever display sanitized text
                if (line.toLowerCase(Locale.ROOT).contains(lowerSearchText)) {
                    results.add(new SearchResult(
                            filePath.toAbsolutePath().toString(),
                            lineNumber,
                            sanitizeForDisplay(line.strip())
                    ));
                    totalFound.incrementAndGet();
                }
                lineNumber++;
            }
        } catch (IOException e) {
            // Skip files that can't be read (e.g. access denied)
        }
    }

    /**
     * Makes arbitrary text safe to render in the results table. Replaced with '?':
     * control/format/private-use/unassigned code points, unpaired surrogates, combining
     * marks, U+FFFD, and anything outside the Basic Multilingual Plane (emoji etc.).
     * Truncates to MAX_DISPLAY_LENGTH code points.
     */
    private static String sanitizeForDisplay(String text) {
        if (text == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(Math.min(text.length(), MAX_DISPLAY_LENGTH) + 3);
        int i = 0;
        int count = 0;
        while (i < text.length() && count < MAX_DISPLAY_LENGTH) {
            int cp = text.codePointAt(i);
            i += Character.charCount(cp);
            count++;
            if (cp == '\t') {
                sb.append(' ');
                continue;
            }
            int type = Character.getType(cp);
            boolean unsafe = cp > 0xFFFF
                    || cp == 0xFFFD
                    || Character.isISOControl(cp)
                    || type == Character.FORMAT
                    || type == Character.PRIVATE_USE
                    || type == Character.UNASSIGNED
                    || type == Character.SURROGATE
                    || type == Character.LINE_SEPARATOR
                    || type == Character.PARAGRAPH_SEPARATOR
                    || type == Character.NON_SPACING_MARK
                    || type == Character.ENCLOSING_MARK
                    || type == Character.COMBINING_SPACING_MARK;
            if (unsafe) {
                sb.append('?');
            } else {
                sb.appendCodePoint(cp);
            }
        }
        if (i < text.length()) {
            sb.append("...");
        }
        return sb.toString();
    }

    private void openFileAtLine(SearchResult result) {
        File file = new File(result.getFileName());
        if (file.exists()) {
            ServiceState.instance.getFileService().openFile(file);
            // You could add logic here to jump to the specific line number
            // if you have a method in your FileService to do so
        }
    }

    public void setSearchFieldText(String text) {
        if (text != null && !text.isEmpty()) {
            searchTextField.setText(text);
            searchTextField.selectAll();
            performSearch();
        }
    }
}