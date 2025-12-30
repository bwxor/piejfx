package com.bwxor.piejfx.service;

import com.bwxor.piejfx.factory.TabFactory;
import com.bwxor.piejfx.state.CodeAreaState;
import com.bwxor.piejfx.state.ServiceState;
import com.bwxor.piejfx.state.UIState;
import com.bwxor.plugin.type.NotificationYesNoCancelOption;
import com.bwxor.plugin.type.RemoveSelectedTabFromPaneOption;
import javafx.scene.control.*;
import org.fxmisc.richtext.CodeArea;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class EditorTabPaneService {
    private void resyncCodeAreaIds() {
        UIState uiState = UIState.getInstance();

        for (int i = 0; i < uiState.getEditorTabPane().getTabs().size(); i++) {
            if (uiState.getEditorTabPane().getTabs().get(i).getContent() instanceof CodeArea c) {
                c.setId(String.valueOf(i));
            }
        }
    }

    public void addTabToPane(File file) {
        UIState uiState = UIState.getInstance();
        ServiceState serviceState = ServiceState.getInstance();

        Tab tab = TabFactory.createEditorTab(file.getName());
        tab.setOnCloseRequest(e -> {
            removeSelectedTabFromPane();
            e.consume();
        });
        uiState.getEditorTabPane().getTabs().add(tab);

        resyncCodeAreaIds();

        if (tab.getContent() instanceof CodeArea c) {
            serviceState.getGrammarService().setGrammarToCodeArea(c, file);

            CodeAreaState.IndividualState individualState = CodeAreaState.instance.getIndividualStates().get(Integer.parseInt(c.getId()));
            individualState.setOpenedFile(file);
            individualState.setSaved(true);

            try (BufferedReader bufferedReader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
                c.replaceText(bufferedReader.readAllAsString());
                tab.setText(tab.getText());
            } catch (IOException e) {
                uiState.getEditorTabPane().getTabs().removeLast();
                serviceState.getNotificationService().showNotificationOk("Could not open specified file.");
                throw new RuntimeException();
            }
        }

        uiState.getEditorTabPane().getSelectionModel().select(tab);

    }

    public void addTabToPane(String tabTitle) {
        UIState uiState = UIState.getInstance();

        Tab tab = TabFactory.createEditorTab(tabTitle);
        tab.setOnCloseRequest(e -> {
            removeSelectedTabFromPane();
            e.consume();
        });
        uiState.getEditorTabPane().getTabs().add(tab);

        resyncCodeAreaIds();

        uiState.getEditorTabPane().getSelectionModel().select(tab);
    }

    /**
     * Removes the selected tab from the pane and prompts to a save if the content is not stored in any file.
     *
     * @return a negative response only if the user was prompted for a save and cancelled it.
     */
    public RemoveSelectedTabFromPaneOption removeSelectedTabFromPane() {
        UIState uiState = UIState.getInstance();
        ServiceState serviceState = ServiceState.getInstance();

        CodeAreaState.IndividualState individualState = CodeAreaState.instance.getIndividualStates().get(uiState.getEditorTabPane().getSelectionModel().getSelectedIndex());

        if (!individualState.isSaved()) {
            boolean repeatPrompt;

            do {
                repeatPrompt = false;

                var pickedOption = serviceState.getNotificationService().showNotificationYesNoCancel("Save file before closing?");

                if (pickedOption.equals(NotificationYesNoCancelOption.CANCEL)) {
                    return RemoveSelectedTabFromPaneOption.CANCELLED;
                } else if (pickedOption.equals(NotificationYesNoCancelOption.YES)) {
                    if (!serviceState.getSaveFileService().saveFile()) {
                        repeatPrompt = true;
                    }
                }
            } while (repeatPrompt);
        }

        CodeAreaState.instance.getIndividualStates().remove(individualState);
        uiState.getEditorTabPane().getTabs().remove(uiState.getEditorTabPane().getSelectionModel().getSelectedItem());

        if (uiState.getEditorTabPane().getTabs().isEmpty()) {
            addTabToPane("Untitled");
        }

        resyncCodeAreaIds();

        return RemoveSelectedTabFromPaneOption.REMOVED;
    }
}
