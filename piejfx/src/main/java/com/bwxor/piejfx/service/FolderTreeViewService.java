package com.bwxor.piejfx.service;

import com.bwxor.piejfx.control.FileTreeItem;
import com.bwxor.piejfx.dto.TreeViewStructure;
import com.bwxor.piejfx.mapper.FileToGraphicMapper;
import com.bwxor.piejfx.state.*;
import com.bwxor.piejfx.type.CreationType;
import com.bwxor.plugin.dto.NewFileResponse;
import com.bwxor.plugin.dto.RenameFileResponse;
import com.bwxor.plugin.service.PluginFolderTreeViewService;
import com.bwxor.plugin.type.NewFileOption;
import com.bwxor.plugin.type.NotificationYesNoCancelOption;
import com.bwxor.plugin.type.RenameFileOption;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class FolderTreeViewService implements PluginFolderTreeViewService {
    private boolean firstLaunch = true;

    public void showFolderTreeView() {
        UIState uiState = UIState.instance;
        ServiceState serviceState = ServiceState.instance;

        if (firstLaunch) {
            uiState.getFolderTreeView().addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if (uiState.getFolderTreeView().getSelectionModel().getSelectedIndex() > 0) {
                    if (e.getClickCount() == 2) {
                        File file = ((FileTreeItem) uiState.getFolderTreeView().getSelectionModel().getSelectedItem()).getFile();

                        if (!file.isDirectory()) {
                            serviceState.getFileService().openFile(file);
                        }
                    }
                }
            });
            firstLaunch = false;
        }

        FolderTreeViewState.instance.setTreeViewStructure(new TreeViewStructure());
        fillExpansionState(FolderTreeViewState.instance.getTreeViewStructure(), uiState.getFolderTreeView().getRoot());

        TreeItem treeItem = createTreeItem();

        if (treeItem != null) {
            uiState.getFolderTreeView().setRoot(treeItem);

            if (!uiState.getHorizontalSplitPane().getItems().contains(uiState.getSplitTabPane())) {
                uiState.getHorizontalSplitPane().getItems().addFirst(uiState.getSplitTabPane());
                addDividerPositionManagement();
            }

            if (FolderTreeViewState.instance.getTreeViewStructure() != null) {
                fillTreeViewWithExpansionState(FolderTreeViewState.instance.getTreeViewStructure(), uiState.getFolderTreeView().getRoot());
            }
            uiState.getFolderTreeView().getRoot().setExpanded(true);
        }
    }

    public void toggleFolderTreeView() {
        UIState uiState = UIState.instance;

        if (uiState.getHorizontalSplitPane().getItems().contains(uiState.getSplitTabPane())) {
            FolderTreeViewState.instance.setTreeViewStructure(new TreeViewStructure());
            fillExpansionState(FolderTreeViewState.instance.getTreeViewStructure(), uiState.getFolderTreeView().getRoot());
            uiState.getHorizontalSplitPane().getItems().remove(uiState.getSplitTabPane());
        } else {
            uiState.getFolderTreeView().setRoot(createTreeItem());
            uiState.getHorizontalSplitPane().getItems().addFirst(uiState.getSplitTabPane());
            addDividerPositionManagement();

            if (FolderTreeViewState.instance.getTreeViewStructure() != null) {
                fillTreeViewWithExpansionState(FolderTreeViewState.instance.getTreeViewStructure(), uiState.getFolderTreeView().getRoot());
            }
        }
    }

    public void addDividerPositionManagement() {
        UIState uiState = UIState.instance;
        MaximizeState maximizeState = MaximizeState.instance;
        HorizontalSplitPaneDividerState horizontalSplitPaneDividerState = HorizontalSplitPaneDividerState.instance;

        uiState.getHorizontalSplitPane().setDividerPosition(0, maximizeState.isMaximized() ? horizontalSplitPaneDividerState.getMaximizedPos() : horizontalSplitPaneDividerState.getNormalPos());

        uiState.getHorizontalSplitPane().getDividers().getFirst().positionProperty().addListener((obs, oldPos, newPos) -> {
            if (maximizeState.isMaximized()) {
                horizontalSplitPaneDividerState.setMaximizedPos(newPos.doubleValue());
            } else {
                horizontalSplitPaneDividerState.setNormalPos(newPos.doubleValue());
            }
        });
    }

    // ------------------------------------------------------------------ watching

    /**
     * Re-reads every loaded folder from disk and updates the tree in place, without
     * rebuilding it and without firing any plugin events. Keeps expansion, selection and scroll.
     * Plugins can call this right after an operation if they don't want to wait for the watcher.
     */
    public void refreshFolderTreeView() {
        FileTreeItem root = openedRoot();
        if (root != null) {
            refreshRecursively(root);
        }
    }

    // ------------------------------------------------------------------ tree building

    public TreeItem createTreeItem() {
        File rootFile = FolderTreeViewState.instance.getOpenedFolder();

        if (rootFile == null) {
            return null;
        }

        FileTreeItem root = createNode(rootFile, rootFile.isDirectory());

        // One handler on the root instead of one per folder: expansion events bubble up from any
        // descendant. Re-syncing on every expand (not only the first) also covers folders the
        // watcher ignores, like node_modules or target.
        EventHandler<TreeItem.TreeModificationEvent<Object>> onExpand = e -> {
            Object source = e.getTreeItem();
            if (source instanceof FileTreeItem item && item.getFile() != null) {
                syncChildren(item);
            }
        };
        ((TreeItem) root).addEventHandler(TreeItem.branchExpandedEvent(), onExpand);

        return root;
    }

    private FileTreeItem createNode(File file, boolean isDirectory) {
        FileTreeItem node = new FileTreeItem(file.getName(), file);

        if (isDirectory) {
            ensurePlaceholder(node);
        }

        node.setGraphic(FileToGraphicMapper.mapFileToGraphic(file));

        return node;
    }

    /**
     * A folder is "loaded" once its real children are in the tree. Unloaded folders hold either
     * nothing or a single placeholder (a FileTreeItem with no file) to show the expand arrow.
     */
    private boolean isLoaded(FileTreeItem node) {
        ObservableList<TreeItem> children = children(node);
        return node.isExpanded()
                || (!children.isEmpty() && ((FileTreeItem) children.getFirst()).getFile() != null);
    }

    private void syncNode(FileTreeItem node) {
        if (isLoaded(node)) {
            syncChildren(node);
        } else {
            ensurePlaceholder(node);
        }
    }

    /**
     * Unloaded folder: show an expand arrow if and only if it has something inside.
     */
    private void ensurePlaceholder(FileTreeItem node) {
        ObservableList<TreeItem> children = children(node);
        boolean hasEntries = hasEntries(node.getFile());

        if (hasEntries && children.isEmpty()) {
            children.add(new FileTreeItem());
        } else if (!hasEntries && !children.isEmpty()) {
            children.clear();
        }
    }

    /**
     * Makes a folder's children match the disk: removes what's gone, inserts what's new at its
     * sorted position, and leaves existing items untouched (so their expansion state and the
     * selection survive).
     */
    private void syncChildren(FileTreeItem node) {
        File[] listed = node.getFile().listFiles();
        if (listed == null) {
            return; // not a directory anymore / gone; the parent's sync will remove it
        }

        List<Entry> desired = sortedEntries(listed);
        Set<File> desiredFiles = new HashSet<>();
        for (Entry entry : desired) {
            desiredFiles.add(entry.file());
        }

        ObservableList<TreeItem> children = children(node);
        children.removeIf(c -> {
            File f = ((FileTreeItem) c).getFile();
            return f == null || !desiredFiles.contains(f); // placeholder or deleted
        });

        Map<File, TreeItem> existing = new HashMap<>();
        for (TreeItem c : children) {
            existing.put(((FileTreeItem) c).getFile(), c);
        }

        for (int i = 0; i < desired.size(); i++) {
            Entry entry = desired.get(i);

            if (i < children.size() && entry.file().equals(((FileTreeItem) children.get(i)).getFile())) {
                continue; // already in place
            }

            TreeItem item = existing.get(entry.file());
            if (item != null) {
                children.remove(item); // out of order (rare); move it
            } else {
                item = createNode(entry.file(), entry.directory());
            }
            children.add(i, item);
        }
    }

    private void refreshRecursively(FileTreeItem node) {
        if (!isLoaded(node)) {
            ensurePlaceholder(node);
            return;
        }

        syncChildren(node);

        for (TreeItem child : List.copyOf(children(node))) {
            if (child instanceof FileTreeItem item && item.getFile() != null && item.getFile().isDirectory()) {
                refreshRecursively(item);
            }
        }
    }

    /**
     * Walks from the root towards {@code dir} through loaded folders only. Returns the folder's
     * own node, or the deepest loaded/unloaded ancestor on the way (syncing that is enough).
     */
    private FileTreeItem findDeepestNode(FileTreeItem root, Path dir) {
        Path rootPath = normalize(root.getFile());
        if (!dir.startsWith(rootPath)) {
            return null;
        }

        FileTreeItem node = root;
        for (Path segment : rootPath.relativize(dir)) {
            if (segment.toString().isEmpty() || !isLoaded(node)) {
                return node;
            }

            FileTreeItem next = null;
            for (TreeItem child : children(node)) {
                File f = ((FileTreeItem) child).getFile();
                if (f != null && f.getName().equals(segment.toString())) {
                    next = (FileTreeItem) child;
                    break;
                }
            }

            if (next == null) {
                return node; // folder not in the tree yet; syncing the parent will add it
            }
            node = next;
        }
        return node;
    }

    // ------------------------------------------------------------------ helpers

    private record Entry(File file, boolean directory) {
    }

    /**
     * Folders first, then case-insensitive by name. isDirectory() is read once per file.
     */
    private static List<Entry> sortedEntries(File[] files) {
        List<Entry> entries = new ArrayList<>(files.length);
        for (File f : files) {
            entries.add(new Entry(f, f.isDirectory()));
        }
        entries.sort(Comparator.comparing((Entry e) -> !e.directory())
                .thenComparing(e -> e.file().getName(), String.CASE_INSENSITIVE_ORDER));
        return entries;
    }

    private static boolean hasEntries(File dir) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir.toPath())) {
            return stream.iterator().hasNext();
        } catch (IOException | RuntimeException e) {
            return false;
        }
    }

    private static Path normalize(File file) {
        return file.toPath().toAbsolutePath().normalize();
    }

    private static FileTreeItem openedRoot() {
        Object root = UIState.instance.getFolderTreeView().getRoot();
        if (root instanceof FileTreeItem item && item.getFile() != null) {
            return item;
        }
        return null;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static ObservableList<TreeItem> children(TreeItem item) {
        return item.getChildren();
    }

    // ------------------------------------------------------------------ expansion state (unchanged)

    private void fillExpansionState(TreeViewStructure treeViewStructure, TreeItem treeItem) {
        if (treeItem == null) {
            return;
        }

        treeViewStructure.setChildren(new ArrayList<>());

        for (int i = 0; i < treeItem.getChildren().size(); i++) {
            TreeItem child = (TreeItem) treeItem.getChildren().get(i);
            TreeViewStructure newItem = new TreeViewStructure();
            newItem.setFile(((FileTreeItem) child).getFile());
            if (child.isExpanded()) {
                newItem.setExpanded(true);
                treeViewStructure.getChildren().add(newItem);
                fillExpansionState(newItem, child);
            }
        }
    }

    private void fillTreeViewWithExpansionState(TreeViewStructure treeViewStructure, TreeItem treeItem) {
        ObservableList<TreeItem> children = treeItem.getChildren();

        if (treeViewStructure.getChildren() != null) {
            treeItem.setExpanded(true);

            for (int i = 0; i < children.size(); i++) {
                for (TreeViewStructure s : treeViewStructure.getChildren()) {
                    FileTreeItem f = (FileTreeItem) children.get(i);
                    if (f.getFile() != null && f.getFile().equals(s.getFile())
                            && s.isExpanded()) {
                        children.get(i).setExpanded(true);
                        fillTreeViewWithExpansionState(s, children.get(i));
                    }
                }
            }
        }
    }

    public void createFile(CreationType creationType, TreeView folderTreeView) {
        ServiceState serviceState = ServiceState.instance;

        String windowTitle;

        if (creationType.equals(CreationType.FILE)) {
            windowTitle = "New File";
        } else {
            windowTitle = "New Folder";
        }

        NewFileResponse response = serviceState.getFileService().showNewFileWindow(windowTitle);

        try {
            File fileToCreate;

            if (response != null && response.option().equals(NewFileOption.CREATE)) {

                if (folderTreeView.getSelectionModel().getSelectedIndex() > 0) {
                    File file = ((FileTreeItem) folderTreeView.getSelectionModel().getSelectedItem()).getFile();

                    fileToCreate = new File(Paths.get(file.isDirectory() ? file.toString() : file.getParentFile().toString(), response.fileName()).toUri());

                    if (!fileToCreate.exists()) {
                        if (creationType.equals(CreationType.FILE)) {
                            fileToCreate.createNewFile();
                            serviceState.getPluginService().invokeOnCreateFile(fileToCreate);
                        } else {
                            fileToCreate.mkdir();
                            serviceState.getPluginService().invokeOnCreateFolder(fileToCreate);
                        }

                        FileTreeItem fileTreeItem = new FileTreeItem(fileToCreate.getName(), fileToCreate);
                        if (file.isDirectory()) {
                            ((FileTreeItem) folderTreeView.getSelectionModel().getSelectedItem()).getChildren().add(fileTreeItem);
                        } else {
                            ((FileTreeItem) folderTreeView.getSelectionModel().getSelectedItem()).getParent().getChildren().add(fileTreeItem);
                        }

                        fileTreeItem.setGraphic(FileToGraphicMapper.mapFileToGraphic(fileToCreate));
                    } else {
                        serviceState.getNotificationService().showNotificationOk("File already exists.");
                    }
                } else {
                    fileToCreate = new File(Paths.get(FolderTreeViewState.instance.getOpenedFolder().toString(), response.fileName()).toUri());

                    if (!fileToCreate.exists()) {
                        if (creationType.equals(CreationType.FILE)) {
                            fileToCreate.createNewFile();
                            serviceState.getPluginService().invokeOnCreateFile(fileToCreate);
                        } else {
                            fileToCreate.mkdir();
                            serviceState.getPluginService().invokeOnCreateFolder(fileToCreate);
                        }

                        var fileChildren = new FileTreeItem(fileToCreate.getName(), fileToCreate);
                        fileChildren.setGraphic(FileToGraphicMapper.mapFileToGraphic(fileToCreate));
                        ((FileTreeItem) folderTreeView.getRoot()).getChildren().add(fileChildren);
                    } else {
                        serviceState.getNotificationService().showNotificationOk("File already exists.");
                    }
                }
            }
        } catch (IOException ex) {
            serviceState.getNotificationService().showNotificationOk("Error while trying to create the file.");
        }
    }

    public void renameFile(TreeView folderTreeView) {
        ServiceState serviceState = ServiceState.instance;
        CodeAreaState codeAreaState = CodeAreaState.instance;
        UIState uiState = UIState.instance;

        String itemViewPrefix;

        File file = ((FileTreeItem) folderTreeView.getSelectionModel().getSelectedItem()).getFile();

        if (file.isDirectory()) {
            itemViewPrefix = "\uD83D\uDCC1";
        } else {
            itemViewPrefix = "";
        }

        RenameFileResponse response = serviceState.getFileService().showRenameFileWindow(file.getName());

        File fileWithPotentialNewName;

        if (response != null && response.option().equals(RenameFileOption.RENAME)) {
            if (folderTreeView.getSelectionModel().getSelectedIndex() > 0) {
                fileWithPotentialNewName = new File(Paths.get(file.getParentFile().toString(), response.newName()).toUri());

                if (!fileWithPotentialNewName.exists()) {
                    var treeItemToRename = (FileTreeItem) folderTreeView.getSelectionModel().getSelectedItem();
                    treeItemToRename.setValue(itemViewPrefix + " " + fileWithPotentialNewName.getName());
                    treeItemToRename.setFile(fileWithPotentialNewName);

                    // If a file, update it in the TabPane and title bar view
                    if (!file.isDirectory()) {
                        for (int i = 0; i < codeAreaState.getIndividualStates().size(); i++) {
                            var state = codeAreaState.getIndividualStates().get(i);

                            if (state.getOpenedFile() != null && state.getOpenedFile().equals(file)) {
                                state.setOpenedFile(fileWithPotentialNewName);
                                uiState.getEditorTabPane().getTabs().get(i).setText(fileWithPotentialNewName.getName());
                                uiState.getEditorTabPane().getTabs().get(i).setGraphic(FileToGraphicMapper.mapFileToGraphic(fileWithPotentialNewName));
                                if (uiState.getEditorTabPane().getSelectionModel().getSelectedIndex() == i) {
                                    uiState.getTitleBarLabel().setText(fileWithPotentialNewName.getName());
                                }
                            }
                        }
                    } else {
                        // If directory, recursively update all child TreeItems
                        updateTreeItemPathsRecursively(treeItemToRename, file.getAbsolutePath(), fileWithPotentialNewName.getAbsolutePath());

                        // Update TreeViewStructure to maintain expansion state
                        FolderTreeViewState folderTreeViewState = FolderTreeViewState.instance;
                        if (folderTreeViewState.getTreeViewStructure() != null) {
                            updateTreeViewStructurePathsRecursively(folderTreeViewState.getTreeViewStructure(),
                                    file.getAbsolutePath(), fileWithPotentialNewName.getAbsolutePath());
                        }

                        String oldDirPath = file.getAbsolutePath();
                        String newDirPath = fileWithPotentialNewName.getAbsolutePath();

                        for (int i = 0; i < codeAreaState.getIndividualStates().size(); i++) {
                            var state = codeAreaState.getIndividualStates().get(i);

                            if (state.getOpenedFile() != null) {
                                String openedFilePath = state.getOpenedFile().getAbsolutePath();

                                if (openedFilePath.startsWith(oldDirPath + File.separator) || openedFilePath.equals(oldDirPath)) {
                                    String relativePath = openedFilePath.substring(oldDirPath.length());
                                    File newFile = new File(newDirPath + relativePath);

                                    state.setOpenedFile(newFile);

                                    uiState.getEditorTabPane().getTabs().get(i).setText(newFile.getName());
                                    uiState.getEditorTabPane().getTabs().get(i).setGraphic(FileToGraphicMapper.mapFileToGraphic(newFile));

                                    if (uiState.getEditorTabPane().getSelectionModel().getSelectedIndex() == i) {
                                        uiState.getTitleBarLabel().setText(newFile.getName());
                                    }
                                }
                            }
                        }
                    }

                    file.renameTo(fileWithPotentialNewName);

                    serviceState.getPluginService().invokeOnRenameFile(file);
                } else {
                    serviceState.getNotificationService().showNotificationOk("File already exists.");
                }
            } else {
                serviceState.getNotificationService().showNotificationOk("You can only rename non-root files.");
            }
        }
    }

    public void deleteFiles(TreeView folderTreeView) {
        ServiceState serviceState = ServiceState.instance;

        var selectedItems = new ArrayList<>(folderTreeView.getSelectionModel().getSelectedItems());

        if (folderTreeView.getSelectionModel().getSelectedIndices().contains(0)) {
            serviceState.getNotificationService().showNotificationOk("You cannot delete the root folder.");
            return;
        }

        List<Path> selectedPaths = new ArrayList<>();
        for (Object s : selectedItems) {
            selectedPaths.add(((FileTreeItem) s).getFile().toPath().toAbsolutePath().normalize());
        }

        selectedItems.removeIf(s -> {
            Path p = ((FileTreeItem) s).getFile().toPath().toAbsolutePath().normalize();
            return selectedPaths.stream().anyMatch(other -> !other.equals(p) && p.startsWith(other));
        });

        NotificationYesNoCancelOption response = serviceState.getNotificationService().showNotificationYesNoCancel("Are you sure you want to delete the file(s)?");

        if (response.equals(NotificationYesNoCancelOption.YES)) {
            for (var s : selectedItems) {
                deleteIndividually((FileTreeItem) s, folderTreeView.getRoot());
            }
        }

        /*
        Even if the selected files were deleted, the internal state of the TreeView still keeps multiple files as
        selected, leading to inconsistencies in the context menu.
         */
        if (folderTreeView.getSelectionModel().getSelectedItems().size() > 0) {
            folderTreeView.getSelectionModel().select(folderTreeView.getSelectionModel().getSelectedItems().getFirst());
        }
    }

    public void deleteIndividually(FileTreeItem treeItem, TreeItem root) {
        ServiceState serviceState = ServiceState.instance;

        if (treeItem.equals(root)) {
            return;
        }


        if (treeItem.getFile().isDirectory()) {
            if (!serviceState.getFileService().deleteFolder(treeItem.getFile())) {
                serviceState.getNotificationService().showNotificationOk("Could not delete file.");
            } else {
                treeItem.getParent().getChildren().remove(treeItem);
                serviceState.getPluginService().invokeOnDeleteFile(treeItem.getFile());
            }
        } else {
            boolean deletionStatus = treeItem.getFile().delete();

            if (!deletionStatus) {
                serviceState.getNotificationService().showNotificationOk("Could not delete file.");
            } else {
                treeItem.getParent().getChildren().remove(treeItem);
                serviceState.getPluginService().invokeOnDeleteFile(treeItem.getFile());
            }
        }
    }

    public void copyFiles(TreeView folderTreeView) {
        ClipboardContent clipboardContent = new ClipboardContent();

        var selectedItems = folderTreeView.getSelectionModel().getSelectedItems();

        var list = new ArrayList<File>();

        for (var s : selectedItems) {
            list.add(((FileTreeItem)s).getFile());
        }

        if (list.isEmpty()) {
            return;
        }

        clipboardContent.putFiles(list);
        Clipboard.getSystemClipboard().setContent(clipboardContent);
    }

    public void pasteFiles(TreeView folderTreeView) {
        FileTreeItem treeItem = (FileTreeItem) folderTreeView.getSelectionModel().getSelectedItem();

        File parent = treeItem.getFile().isDirectory() ? treeItem.getFile() : treeItem.getFile().getParentFile();

        Clipboard clipboard = Clipboard.getSystemClipboard();
        if (!clipboard.hasFiles()) return;

        List<File> files = clipboard.getFiles();
        for (File src : files) {
            try {
                Path dest = uniqueTarget(parent.toPath().resolve(src.getName()));
                copyRecursive(src.toPath(), dest);
            } catch (IOException e) {
                e.printStackTrace(); // show an error dialog instead
            }
        }

        refreshFolderTreeView();
    }

    private void copyRecursive(Path source, Path target) throws IOException {
        ServiceState serviceState = ServiceState.instance;

        if (Files.isDirectory(source)) {
            if (target.startsWith(source)) {
                serviceState.getNotificationService().showNotificationOk("Cannot paste a folder into itself.");
                return;
            }
            try (var stream = Files.walk(source)) {
                for (Path p : (Iterable<Path>) stream::iterator) {
                    Path dest = target.resolve(source.relativize(p).toString());
                    if (Files.isDirectory(p)) {
                        Files.createDirectories(dest);
                    } else {
                        Files.copy(p, dest, StandardCopyOption.COPY_ATTRIBUTES);
                    }
                }
            }
        } else {
            Files.copy(source, target, StandardCopyOption.COPY_ATTRIBUTES);
        }
    }

    // Avoids overwriting: "report.txt" -> "report (1).txt"
    private Path uniqueTarget(Path path) {
        if (!Files.exists(path)) return path;
        String name = path.getFileName().toString();
        int dot = name.lastIndexOf('.');
        String base = (dot > 0 && !Files.isDirectory(path)) ? name.substring(0, dot) : name;
        String ext = (dot > 0 && !Files.isDirectory(path)) ? name.substring(dot) : "";
        int i = 1;
        Path candidate;
        do {
            candidate = path.resolveSibling(base + " (" + i++ + ")" + ext);
        } while (Files.exists(candidate));
        return candidate;
    }

    /**
     * Recursively updates the file paths of all child TreeItems when a directory is renamed.
     * For unexpanded directories, clears children and collapses them so they'll be properly
     * reloaded with the new path when expanded again.
     *
     * @param treeItem The TreeItem representing the renamed directory
     * @param oldPath  The old absolute path of the directory
     * @param newPath  The new absolute path of the directory
     */
    private static void updateTreeItemPathsRecursively(FileTreeItem treeItem, String oldPath, String newPath) {
        for (var child : treeItem.getChildren()) {
            if (child instanceof FileTreeItem) {
                FileTreeItem fileTreeItem = (FileTreeItem) child;
                File oldFile = fileTreeItem.getFile();

                if (oldFile != null) {
                    String oldFilePath = oldFile.getAbsolutePath();

                    String relativePath = oldFilePath.substring(oldPath.length());
                    File newFile = new File(newPath + relativePath);

                    fileTreeItem.setFile(newFile);

                    if (oldFile.isDirectory()) {
                        if (fileTreeItem.isExpanded() && fileTreeItem.getChildren().size() > 0) {
                            // Directory is expanded, recursively update all children
                            updateTreeItemPathsRecursively(fileTreeItem, oldFilePath, newFile.getAbsolutePath());
                        } else if (fileTreeItem.getChildren().size() > 0) {
                            // Directory is not expanded but has placeholder children
                            // Clear them and collapse so they'll be reloaded with correct paths on next expansion
                            fileTreeItem.getChildren().clear();
                            fileTreeItem.setExpanded(false);

                            // Add new placeholders based on the new directory path
                            File[] files = newFile.listFiles();
                            if (files != null && files.length > 0) {
                                // Add a single placeholder to indicate the folder has children
                                fileTreeItem.getChildren().add(new FileTreeItem());
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Recursively updates the file paths in TreeViewStructure when a directory is renamed.
     * This ensures that the expansion state is preserved correctly when the tree is refreshed.
     *
     * @param treeViewStructure The TreeViewStructure to update
     * @param oldPath           The old absolute path of the directory
     * @param newPath           The new absolute path of the directory
     */
    private static void updateTreeViewStructurePathsRecursively(TreeViewStructure treeViewStructure, String oldPath, String newPath) {
        if (treeViewStructure == null) {
            return;
        }

        // Update the file reference if it matches the old path or is within it
        if (treeViewStructure.getFile() != null) {
            String filePath = treeViewStructure.getFile().getAbsolutePath();

            if (filePath.equals(oldPath) || filePath.startsWith(oldPath + File.separator)) {
                String relativePath = filePath.substring(oldPath.length());
                File newFile = new File(newPath + relativePath);
                treeViewStructure.setFile(newFile);
            }
        }

        // Recursively update all children
        if (treeViewStructure.getChildren() != null) {
            for (TreeViewStructure child : treeViewStructure.getChildren()) {
                updateTreeViewStructurePathsRecursively(child, oldPath, newPath);
            }
        }
    }
}