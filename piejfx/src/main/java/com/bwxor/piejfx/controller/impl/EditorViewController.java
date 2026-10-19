package com.bwxor.piejfx.controller.impl;

import com.bwxor.piejfx.controller.MaximizableViewController;
import com.bwxor.piejfx.factory.ContextMenuFactory;
import com.bwxor.piejfx.factory.FindReplaceHBoxFactory;
import com.bwxor.piejfx.service.SearchInFilesDialogService;
import com.bwxor.piejfx.state.*;
import com.bwxor.plugin.type.RemoveSelectedTabFromPaneOption;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class EditorViewController extends MaximizableViewController {
    private List<String> parameters;
    @FXML
    private Label titleBarLabel;
    @FXML
    private SplitPane verticalSplitPane;
    @FXML
    private SplitPane horizontalSplitPane;
    @FXML
    private TabPane editorTabPane;
    @FXML
    private TabPane splitTabPane;
    @FXML
    private TreeView folderTreeView;
    @FXML
    private TabPane terminalTabPane;
    @FXML
    private Menu themesMenu;
    @FXML
    private MenuBar menuBar;
    @FXML
    private MenuItem charsetMenuItem;
    @FXML
    private Label lineColLabel;
    @FXML
    private Label numCharsLabel;
    @FXML
    private MenuButton encodingMenuButton;
    @FXML
    private HBox statusBar;
    @FXML
    private Label wordWrapLabel;

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    public void handleParameters() {
        ServiceState serviceState = ServiceState.instance;
        UIState uiState = UIState.instance;

        if (!parameters.isEmpty()) {
            ServiceState.instance.getFileService().openFile(new File(parameters.getFirst()));
        } else {
            ServiceState.instance.getEditorTabPaneService().addTabToPane("Untitled");
        }

        titleBarLabel.setText(editorTabPane.getSelectionModel().getSelectedItem().getText());

        verticalSplitPane.getItems().remove(terminalTabPane);
        horizontalSplitPane.getItems().remove(splitTabPane);
        editorTabPane.setContextMenu(ContextMenuFactory.createEditorTabPaneContextMenu());
        terminalTabPane.setContextMenu(ContextMenuFactory.createTerminalTabPaneContextMenu());

        editorTabPane.getSelectionModel().selectedItemProperty().addListener(
                (_, _, t1) -> {
                    if (t1 != null) {
                        var codeArea = (CodeArea) ((VBox) editorTabPane.getSelectionModel().getSelectedItem().getContent()).getChildren().getLast();

                        if (CodeAreaState.instance.getIndividualStates().get(editorTabPane.getSelectionModel().getSelectedIndex()).isSaved()) {
                            titleBarLabel.setText(t1.getText());
                        } else {
                            titleBarLabel.setText(t1.getText().substring(1));
                        }
                        uiState.getLineColLabel().setText((codeArea.getCurrentParagraph() + 1) + ":"  +  (codeArea.getCaretColumn() + 1));
                        uiState.getNumCharsLabel().setText(codeArea.getText().length() + " chars");
                        
                        // Update encoding button to show current tab's encoding
                        updateEncodingButtonText();
                    }
                }
        );

        folderTreeView.setContextMenu(ContextMenuFactory.createFolderTreeViewContextMenu(folderTreeView));
        folderTreeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        folderTreeView.setOnKeyPressed(e -> {
            if (e.isControlDown()) {
                if (e.getCode().equals(KeyCode.C)) {
                    serviceState.getFolderTreeViewService().copyFiles(folderTreeView);
                }
                else if (e.getCode().equals(KeyCode.V)) {
                    serviceState.getFolderTreeViewService().pasteFiles(folderTreeView);
                }
            }
            else
            {
                if (e.getCode().equals(KeyCode.DELETE)) {
                    serviceState.getFolderTreeViewService().deleteFiles(folderTreeView);
                }
            }
        });

        CompletableFuture.runAsync(() ->
                LoadedPluginsState.instance.setPlugins(ServiceState.instance.getPluginService().getPlugins())
        ).thenRun(() ->
                Platform.runLater(() -> ServiceState.instance.getPluginService().invokeOnLoad())
        );
    }

    @FXML
    public void initialize() {
        UIState uiState = UIState.instance;

        uiState.setHorizontalSplitPane(horizontalSplitPane);
        uiState.setVerticalSplitPane(verticalSplitPane);
        uiState.setSplitTabPane(splitTabPane);
        uiState.setFolderTreeView(folderTreeView);
        uiState.setEditorTabPane(editorTabPane);
        uiState.setTerminalTabPane(terminalTabPane);
        uiState.setTitleBarLabel(titleBarLabel);
        uiState.setThemesMenu(themesMenu);
        uiState.setMenuBar(menuBar);
        uiState.setLineColLabel(lineColLabel);
        uiState.setNumCharsLabel(numCharsLabel);
        uiState.setStatusBar(statusBar);

        // Apply status bar visibility from config
        boolean statusBarVisible = StatusBarState.instance.isStatusBarVisible();
        statusBar.setVisible(statusBarVisible);
        statusBar.setManaged(statusBarVisible);

        // Initialize word wrap label state
        updateWordWrapLabelText();

        ServiceState.instance.getThemeService().loadMenuWithThemes(ThemeState.instance.getThemes());
    }

    @FXML
    public void onCloseButtonClick() {
        int size = editorTabPane.getTabs().size();
        editorTabPane.getSelectionModel().select(size - 1);

        for (int i = 0; i < size; i++) {
            var saveResponse = ServiceState.instance.getEditorTabPaneService().removeSelectedTabFromPane();
            if (saveResponse.equals(RemoveSelectedTabFromPaneOption.CANCELLED)) {
                return;
            }
        }

        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
        ServiceState.instance.getStartStopService().close();
    }

    @FXML
    public void onNewButtonClickEvent() {
        ServiceState.instance.getEditorTabPaneService().addTabToPane("Untitled");
    }

    @FXML
    public void onOpenButtonClickEvent() {
        ServiceState.instance.getFileService().openFile();
    }

    @FXML
    public void onOpenFolderButtonClickEvent() {
        ServiceState.instance.getFileService().openFolder();
    }

    @FXML
    public void onSaveButtonClickEvent() {
        ServiceState.instance.getFileService().saveFile();
    }

    @FXML
    public void onSaveAsButtonClickEvent() {
        ServiceState.instance.getFileService().saveFileAs();
    }

    @FXML
    public void onManagePluginsButtonClickEvent() {
        ServiceState.instance.getGetPluginsViewService().showManagePluginsView();
    }

    @FXML
    public void onAboutButtonClickEvent() {
        ServiceState.instance.getViewService().displayView("about-view.fxml");
    }

    @FXML
    public void onCheatsheetButtonClickEvent() {
        ServiceState.instance.getViewService().displayView("cheatsheet-view.fxml");
    }

    @FXML
    public void onWordWrapToggle(MouseEvent event) {
        boolean newWordWrapState = !WordWrapState.instance.isWordWrapEnabled();
        WordWrapState.instance.setWordWrapEnabled(newWordWrapState);

        // Apply word wrap to all open code areas
        TabPane editorTabPane = UIState.instance.getEditorTabPane();
        if (editorTabPane != null) {
            for (Tab tab : editorTabPane.getTabs()) {
                VBox vBox = (VBox) tab.getContent();
                CodeArea codeArea = (CodeArea) vBox.getChildren().getLast();
                codeArea.setWrapText(newWordWrapState);
            }
        }

        // Save the configuration
        ServiceState.instance.getConfigurationService().rewriteConfig();

        // Update the label text
        updateWordWrapLabelText();
    }

    private void updateWordWrapLabelText() {
        wordWrapLabel.setText(WordWrapState.instance.isWordWrapEnabled() ? "Wrap: ON" : "Wrap: OFF");
    }

    @FXML
    public void onKeyPressed(KeyEvent keyEvent) {
        UIState uiState = UIState.instance;

        ServiceState.instance.getPluginService().invokeOnKeyPress(keyEvent);

        if (keyEvent.isControlDown() && keyEvent.isShiftDown()) { // Ctrl+Shift modifiers
            if (keyEvent.getCode().equals(KeyCode.N)) {
                ServiceState.instance.getFileService().showFileSearchWindow();
            } else if (keyEvent.getCode().equals(KeyCode.F)) {
                // Ctrl+Shift+F - Search in files
                // Check if a folder is opened
                if (FolderTreeViewState.instance.getOpenedFolder() == null) {
                    ServiceState.instance.getNotificationService().showNotificationOk("No folder is currently opened.");
                    return;
                }

                SearchInFilesDialogService searchService = SearchInFilesDialogService.getInstance();
                File openedFolder = FolderTreeViewState.instance.getOpenedFolder();
                searchService.setWorkspaceRoot(openedFolder);
                searchService.showSearchDialog((Stage) editorTabPane.getScene().getWindow());
            }
        } else if (keyEvent.isControlDown()) { // Ctrl modifier only
            if (keyEvent.getCode().equals(KeyCode.T)) {
                ServiceState.instance.getEditorTabPaneService().addTabToPane("Untitled");
            } else if (keyEvent.getCode().equals(KeyCode.W)) {
                ServiceState.instance.getEditorTabPaneService().removeSelectedTabFromPane();
            } else if (keyEvent.getCode().equals(KeyCode.S)) {
                ServiceState.instance.getFileService().saveFile();
            } else if (keyEvent.getCode().equals(KeyCode.B)) {
                ServiceState.instance.getTerminalTabPaneService().toggleTerminalTabPane();
            } else if (keyEvent.getCode().equals(KeyCode.G)) {
                if (FolderTreeViewState.instance.getOpenedFolder() != null) {
                    ServiceState.instance.getFolderTreeViewService().toggleFolderTreeView();
                } else {
                    ServiceState.instance.getFileService().openFolder();
                }
            } else if (keyEvent.getCode().equals(KeyCode.F)) {
                VBox vBox = (VBox) uiState.getEditorTabPane().getSelectionModel().getSelectedItem().getContent();

                var codeArea = (CodeArea) ((VBox) editorTabPane.getSelectionModel().getSelectedItem().getContent()).getChildren().getLast();

                if (vBox.getChildren().size() == 1) {
                    vBox.getChildren().add(0, FindReplaceHBoxFactory.createFindReplaceHBox(codeArea.getSelectedText()));
                    ((HBox) vBox.getChildren().getFirst()).getChildren().getFirst().requestFocus();
                } else {
                    vBox.getChildren().removeFirst();
                }
            } else if (keyEvent.getCode().equals(KeyCode.R)) {
                VBox vBox = (VBox) uiState.getEditorTabPane().getSelectionModel().getSelectedItem().getContent();

                var codeArea = (CodeArea) ((VBox) editorTabPane.getSelectionModel().getSelectedItem().getContent()).getChildren().getLast();

                if (vBox.getChildren().size() == 1) {
                    vBox.getChildren().add(0, FindReplaceHBoxFactory.createFindReplaceHBox(codeArea.getSelectedText()));
                    ((HBox) vBox.getChildren().getFirst()).getChildren().get(1).requestFocus();
                } else {
                    vBox.getChildren().removeFirst();
                }
            }
        }
    }

    /**
     * Updates the encoding button text to reflect the current tab's charset.
     */
    private void updateEncodingButtonText() {
        int selectedIndex = editorTabPane.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < CodeAreaState.instance.getIndividualStates().size()) {
            Charset charset = CodeAreaState.instance.getIndividualStates().get(selectedIndex).getCharset();
            encodingMenuButton.setText(charset.displayName());
        }
    }

    @FXML
    public void onEncodingSelected(ActionEvent actionEvent) {
        UIState uiState = UIState.instance;
        ServiceState serviceState = ServiceState.instance;
        
        MenuItem selectedItem = (MenuItem) actionEvent.getSource();
        String charsetName = selectedItem.getText();
        Charset selectedCharset = Charset.forName(charsetName);
        
        int selectedTabIndex = uiState.getEditorTabPane().getSelectionModel().getSelectedIndex();
        if (selectedTabIndex < 0) {
            return;
        }
        
        CodeAreaState.IndividualState codeAreaState = CodeAreaState.instance.getIndividualStates().get(selectedTabIndex);
        Tab selectedTab = uiState.getEditorTabPane().getSelectionModel().getSelectedItem();
        
        if (!(selectedTab.getContent() instanceof VBox vBox) || !(vBox.getChildren().getLast() instanceof CodeArea codeArea)) {
            return;
        }
        
        // If it's an opened file, re-read it with the new encoding
        if (codeAreaState.getOpenedFile() != null) {
            try {
                String content = new String(Files.readAllBytes(codeAreaState.getOpenedFile().toPath()), selectedCharset);
                // Strip BOM if present
                if (content.startsWith("\uFEFF")) {
                    content = content.substring(1);
                }
                
                codeArea.replaceText(content);
                codeArea.moveTo(0);
                codeArea.requestFollowCaret();
                
                // Update the charset in the state
                codeAreaState.setCharset(selectedCharset);
                encodingMenuButton.setText(selectedCharset.displayName());
                
            } catch (Exception e) {
                serviceState.getNotificationService().showNotificationOk("Error while trying to re-open the file with the selected encoding.");
            }
        } else {
            // For untitled tabs, just set the charset for future saves
            codeAreaState.setCharset(selectedCharset);
            encodingMenuButton.setText(selectedCharset.displayName());
        }
    }
}
