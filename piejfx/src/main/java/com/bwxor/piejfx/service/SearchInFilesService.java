package com.bwxor.piejfx.service;

import com.bwxor.piejfx.dto.SearchInFilesResult;
import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class SearchInFilesService {

    public Task<List<SearchInFilesResult>> searchInFiles(String searchText, File rootDirectory) {
        return new Task<>() {
            @Override
            protected List<SearchInFilesResult> call() throws Exception {
                List<SearchInFilesResult> results = new ArrayList<>();
                
                if (searchText == null || searchText.isEmpty() || rootDirectory == null || !rootDirectory.exists()) {
                    return results;
                }

                try (Stream<Path> paths = Files.walk(rootDirectory.toPath())) {
                    paths.filter(Files::isRegularFile)
                         .filter(path -> isTextFile(path.toFile()))
                         .forEach(path -> {
                             try {
                                 searchInFile(path.toFile(), searchText, results);
                             } catch (IOException e) {
                                 // Skip files that can't be read
                             }
                         });
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return results;
            }
        };
    }

    private void searchInFile(File file, String searchText, List<SearchInFilesResult> results) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                if (line.toLowerCase().contains(searchText.toLowerCase())) {
                    String trimmedLine = line.trim();
                    if (trimmedLine.length() > 100) {
                        trimmedLine = trimmedLine.substring(0, 100) + "...";
                    }
                    
                    results.add(new SearchInFilesResult(
                        file.getName(),
                        lineNumber,
                        trimmedLine,
                        file.getAbsolutePath()
                    ));
                }
                lineNumber++;
            }
        }
    }

    private boolean isTextFile(File file) {
        String name = file.getName().toLowerCase();
        
        // Skip certain directories
        String path = file.getAbsolutePath();
        if (path.contains("/.git/") || path.contains("\\.git\\") ||
            path.contains("/target/") || path.contains("\\target\\") ||
            path.contains("/build/") || path.contains("\\build\\") ||
            path.contains("/node_modules/") || path.contains("\\node_modules\\") ||
            path.contains("/.idea/") || path.contains("\\.idea\\")) {
            return false;
        }

        // Common text file extensions
        return name.endsWith(".java") || name.endsWith(".txt") || name.endsWith(".xml") ||
               name.endsWith(".json") || name.endsWith(".properties") || name.endsWith(".yml") ||
               name.endsWith(".yaml") || name.endsWith(".md") || name.endsWith(".js") ||
               name.endsWith(".ts") || name.endsWith(".html") || name.endsWith(".css") ||
               name.endsWith(".py") || name.endsWith(".c") || name.endsWith(".cpp") ||
               name.endsWith(".h") || name.endsWith(".hpp") || name.endsWith(".cs") ||
               name.endsWith(".go") || name.endsWith(".rs") || name.endsWith(".kt") ||
               name.endsWith(".swift") || name.endsWith(".rb") || name.endsWith(".php") ||
               name.endsWith(".sh") || name.endsWith(".bat") || name.endsWith(".fxml");
    }
}
