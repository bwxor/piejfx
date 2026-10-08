package com.bwxor.piejfx.service;

import com.bwxor.piejfx.control.FileTreeItem;
import com.bwxor.piejfx.dto.TreeViewStructure;
import com.bwxor.piejfx.state.*;
import com.bwxor.plugin.service.PluginFolderTreeViewService;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FolderTreeViewService implements PluginFolderTreeViewService {
    private static final String FOLDER_PREFIX = "\uD83D\uDCC1 ";

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
            }
            else {
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
        if (!isDirectory) {
            return new FileTreeItem(file.getName(), file);
        }

        FileTreeItem node = new FileTreeItem(FOLDER_PREFIX + file.getName(), file);
        ensurePlaceholder(node);
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

    /** Unloaded folder: show an expand arrow if and only if it has something inside. */
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

    private record Entry(File file, boolean directory) {}

    /** Folders first, then case-insensitive by name. isDirectory() is read once per file. */
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
}