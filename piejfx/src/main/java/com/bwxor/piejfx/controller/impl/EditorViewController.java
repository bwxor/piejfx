package com.bwxor.piejfx.controller.impl;

import com.bwxor.piejfx.controller.MaximizableViewController;
import com.bwxor.piejfx.factory.ContextMenuFactory;
import com.bwxor.piejfx.factory.FindReplaceHBoxFactory;
import com.bwxor.piejfx.state.*;
import com.bwxor.plugin.type.RemoveSelectedTabFromPaneOption;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

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


    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    public void handleParameters() {
        if (!parameters.isEmpty()) {
            ServiceState.instance.getFileService().openFile(new File(parameters.getFirst()));
        } else {
            ServiceState.instance.getEditorTabPaneService().addTabToPane("Untitled");
        }

        titleBarLabel.setText(editorTabPane.getSelectionModel().getSelectedItem().getText());

        verticalSplitPane.getItems().remove(terminalTabPane);
        horizontalSplitPane.getItems().remove(splitTabPane);
        ServiceState.instance.getTerminalTabPaneService().addTabToPane(null);
        terminalTabPane.setContextMenu(ContextMenuFactory.createTerminalTabPaneContextMenu());

        editorTabPane.getSelectionModel().selectedItemProperty().addListener(
                (_, _, t1) -> {
                    if (t1 != null) {
                        if (CodeAreaState.instance.getIndividualStates().get(editorTabPane.getSelectionModel().getSelectedIndex()).isSaved()) {
                            titleBarLabel.setText(t1.getText());
                        } else {
                            titleBarLabel.setText(t1.getText().substring(1));
                        }
                    }
                }
        );

        folderTreeView.setContextMenu(ContextMenuFactory.createFolderTreeViewContextMenu(folderTreeView));

        PluginState.instance.setPlugins(ServiceState.instance.getPluginService().getPlugins());
        ServiceState.instance.getPluginService().invokeOnLoad();
    }

    @FXML
    public void initialize() {
        UIState.instance.setHorizontalSplitPane(horizontalSplitPane);
        UIState.instance.setVerticalSplitPane(verticalSplitPane);
        UIState.instance.setSplitTabPane(splitTabPane);
        UIState.instance.setFolderTreeView(folderTreeView);
        UIState.instance.setEditorTabPane(editorTabPane);
        UIState.instance.setTerminalTabPane(terminalTabPane);
        UIState.instance.setTitleBarLabel(titleBarLabel);
        UIState.instance.setThemesMenu(themesMenu);
        UIState.instance.setMenuBar(menuBar);

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
        ServiceState.instance.getCloseService().close();
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
    public void onPluginStoreButtonClickEvent() {
        ServiceState.instance.getViewService().displayView("get-plugins-view.fxml");
    }

    @FXML
    public void onAboutButtonClickEvent() {
        ServiceState.instance.getViewService().displayView("about-view.fxml");
    }

    @FXML
    public void onCheatsheetButtonClickEvent() {
        ServiceState.instance.getViewService().displayView("cheatsheet-view.fxml");;
    }

    @FXML
    public void onKeyPressed(KeyEvent keyEvent) {
        UIState uiState = UIState.instance;

        ServiceState.instance.getPluginService().invokeOnKeyPress(keyEvent);

        if (keyEvent.isControlDown()) { // Modifiers will be handled
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
                VBox vBox = (VBox)uiState.getEditorTabPane().getSelectionModel().getSelectedItem().getContent();

                if (vBox.getChildren().size() == 1) {
                    vBox.getChildren().add(0, FindReplaceHBoxFactory.createFindReplaceHBox());
                    ((HBox)vBox.getChildren().getFirst()).getChildren().getFirst().requestFocus();
                }
                else {
                    vBox.getChildren().removeFirst();
                }
            } else if (keyEvent.getCode().equals(KeyCode.R)) {
                VBox vBox = (VBox)uiState.getEditorTabPane().getSelectionModel().getSelectedItem().getContent();

                if (vBox.getChildren().size() == 1) {
                    vBox.getChildren().add(0, FindReplaceHBoxFactory.createFindReplaceHBox());
                    ((HBox)vBox.getChildren().getFirst()).getChildren().get(1).requestFocus();
                }
                else {
                    vBox.getChildren().removeFirst();
                }
            }
        }
    }
}
