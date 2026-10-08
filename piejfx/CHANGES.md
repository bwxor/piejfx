# Changes

## src/main/java/com/bwxor/piejfx/controller/impl/FileSearchViewController.java
- Runs the "Search for file name" folder scan on a background daemon thread and adds matches to the list in batches on the FX thread.
- Cancels a running scan when the query changes or the window closes; stale results are discarded.
- Prints every searched text to the console (`[search for file name] <text>`) for debugging.

## src/main/java/com/bwxor/piejfx/controller/impl/SearchInFilesViewController.java
- Runs the "Search in files" scan on a background daemon thread and publishes results in batches on the FX thread.
- Cancels a running scan when the query changes or the dialog closes; stale results are discarded.
- Replaced `Files.walk` with `Files.walkFileTree` and a visitor that skips unreadable entries silently (no output, no crash).
- Sanitizes the text shown in the results table (control, format, private-use, unassigned and unpaired-surrogate characters become spaces; long lines are truncated to 300 characters) to avoid a native JavaFX/DirectWrite crash. Matching still uses the raw line.
- Prints every searched text to the console (`[search in files] <text>`) for debugging.
