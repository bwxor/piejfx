# Changes

- `src/main/java/com/bwxor/piejfx/service/FileService.java`: added `openFile(File, int lineNumber)`, which opens the file like `openFile(File)` and then moves the caret to the given 1-based line, selects it and scrolls it into view.
- `src/main/java/com/bwxor/piejfx/controller/impl/SearchInFilesViewController.java`: double-clicking (or pressing Enter on) a result now opens the file at the match's line via `openFile(File, int)` and closes the Search in Files dialog; the file-name column shows the path relative to the opened folder instead of the absolute path.
- `CHANGES.md`: this change list.
