# Changes

## Modified Files

- `src/main/java/com/bwxor/piejfx/controller/impl/EditorViewController.java` - Added folder check notification before opening Search in Files dialog (Ctrl+Shift+F)
- `src/main/resources/com/bwxor/piejfx/view/searchinfiles-view.fxml` - Updated window design with custom title bar, close/minimize buttons, and consistent styling
- `src/main/java/com/bwxor/piejfx/service/SearchInFilesDialogService.java` - Changed stage style to TRANSPARENT for consistent window appearance
- `src/main/java/com/bwxor/piejfx/controller/impl/SearchInFilesViewController.java` - Extended MovableViewController, added window drag handlers, close/minimize button handlers, and ESC/Ctrl+W keyboard shortcuts

## Summary

Fixed the Search in Files dialog to:
1. Check if a folder is opened before displaying, showing a notification if none is open
2. Match the window design and styling of other application windows (file search)
3. Add proper window controls (close, minimize) and keyboard shortcuts
