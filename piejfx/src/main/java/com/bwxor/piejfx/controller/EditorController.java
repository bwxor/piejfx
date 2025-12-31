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
    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    private Menu themesMenu;

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    public void handleParameters() {
        if (!parameters.isEmpty()) {
            ServiceState.getInstance().getFileService().openFile(new File(parameters.getFirst()));
        } else {
            ServiceState.getInstance().getEditorTabPaneService().addTabToPane("Untitled");
        }

        titleBarLabel.setText(editorTabPane.getSelectionModel().getSelectedItem().getText());

        verticalSplitPane.getItems().remove(terminalTabPane);
        horizontalSplitPane.getItems().remove(splitTabPane);
        ServiceState.getInstance().getTerminalTabPaneService().addTabToPane(null);
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
    }

    @FXML
    public void initialize() {
        UIState.getInstance().setHorizontalSplitPane(horizontalSplitPane);
        UIState.getInstance().setVerticalSplitPane(verticalSplitPane);
        UIState.getInstance().setSplitTabPane(splitTabPane);
        UIState.getInstance().setFolderTreeView(folderTreeView);
        UIState.getInstance().setEditorTabPane(editorTabPane);
        UIState.getInstance().setTerminalTabPane(terminalTabPane);
        UIState.getInstance().setTitleBarLabel(titleBarLabel);
        UIState.getInstance().setMenu(themesMenu);

        ServiceState.getInstance().getThemeService().loadMenuWithThemes(ThemeState.instance.getThemes());
    }

    @FXML
    public void onCloseButtonClick() {
        int size = editorTabPane.getTabs().size();
        editorTabPane.getSelectionModel().select(size - 1);

        for (int i = 0; i < size; i++) {
            var saveResponse = ServiceState.getInstance().getEditorTabPaneService().removeSelectedTabFromPane();
            if (saveResponse.equals(RemoveSelectedTabFromPaneOption.CANCELLED)) {
                return;
            }
        }

        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
        ServiceState.getInstance().getCloseService().close();
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
        ServiceState.getInstance().getEditorTabPaneService().addTabToPane("Untitled");
    }

    @FXML
    public void onOpenButtonClickEvent() {
        ServiceState.getInstance().getFileService().openFile();
    }

    @FXML
    public void onOpenFolderButtonClickEvent() {
        ServiceState.getInstance().getFileService().openFolder();
    }

    @FXML
    public void onSaveButtonClickEvent() {
        ServiceState.getInstance().getSaveFileService().saveFile();
    }

    @FXML
    public void onSaveAsButtonClickEvent() {
        ServiceState.getInstance().getSaveFileService().saveFileAs();
    }

    @FXML
    public void onAboutButtonClickEvent() {
        ServiceState.getInstance().getAboutService().showAboutPage();
    }

    @FXML
    public void onKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.isControlDown()) { // Modifiers will be handled
            if (keyEvent.getCode().equals(KeyCode.T)) {
                ServiceState.getInstance().getEditorTabPaneService().addTabToPane("Untitled");
            } else if (keyEvent.getCode().equals(KeyCode.W)) {
                ServiceState.getInstance().getEditorTabPaneService().removeSelectedTabFromPane();
            } else if (keyEvent.getCode().equals(KeyCode.S)) {
                ServiceState.getInstance().getSaveFileService().saveFile();
            } else if (keyEvent.getCode().equals(KeyCode.B)) {
                ServiceState.getInstance().getTerminalTabPaneService().toggleTerminalTabPane();
            } else if (keyEvent.getCode().equals(KeyCode.G)) {
                if (FolderTreeViewState.instance.getOpenedFolder() != null) {
                    ServiceState.getInstance().getFolderTreeViewService().toggleFolderTreeView();
                } else {
                    ServiceState.getInstance().getFileService().openFolder();
                }
            }
        }
    }
}
