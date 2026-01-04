package com.bwxor.piejfx.controller;

import com.bwxor.piejfx.factory.ContextMenuFactory;
import com.bwxor.piejfx.state.*;
import com.bwxor.plugin.type.RemoveSelectedTabFromPaneOption;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class EditorController {
    private List<String> parameters;
    @FXML
    private AnchorPane titleBarAnchorPane;
    @FXML
    private Label titleBarLabel;
    @FXML
    private Button closeButton;
    @FXML
    private Button maximizeButton;
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
    private Menu pluginsMenu;

    private double xOffset = 0;
    private double yOffset = 0;


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
        UIState.instance.setPluginsMenu(pluginsMenu);

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
    public void onMaximizeButtonClick() {
        MaximizeState.instance.toggleMaximize(StageState.instance.getStage(), maximizeButton);
    }

    @FXML
    public void onMinimizeButtonClick() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    public void handleDoubleClickAction(MouseEvent mouseEvent) {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            if (mouseEvent.getClickCount() == 2) {
                MaximizeState.instance.toggleMaximize(StageState.instance.getStage(), maximizeButton);
            }
        }
    }

    @FXML
    public void handleClickAction(MouseEvent mouseEvent) {
        if (!MaximizeState.instance.isMaximized()) {
            xOffset = mouseEvent.getSceneX();
            yOffset = mouseEvent.getSceneY();
        }
    }

    @FXML
    public void handleMovementAction(MouseEvent mouseEvent) {
        if (!MaximizeState.instance.isMaximized()) {
            Stage stage = (Stage) titleBarAnchorPane.getScene().getWindow();
            stage.setX(mouseEvent.getScreenX() - xOffset);
            stage.setY(mouseEvent.getScreenY() - yOffset);
        }
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
    public void onAboutButtonClickEvent() {
        ServiceState.instance.getAboutService().showAboutPage();
    }

    @FXML
    public void onCheatsheetButtonClickEvent() {
        ServiceState.instance.getCheatsheetService().showCheatsheetPage();
    }

    @FXML
    public void onKeyPressed(KeyEvent keyEvent) {
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
            }
        }
    }
}
