package com.bwxor.piejfx.service;

import com.bwxor.piejfx.factory.TabFactory;
import com.bwxor.piejfx.mapper.FileToGraphicMapper;
import com.bwxor.piejfx.state.CodeAreaState;
import com.bwxor.piejfx.state.ServiceState;
import com.bwxor.piejfx.state.UIState;
import com.bwxor.plugin.service.PluginEditorTabPaneService;
import com.bwxor.plugin.type.NotificationYesNoCancelOption;
import com.bwxor.plugin.type.RemoveSelectedTabFromPaneOption;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.fxmisc.richtext.CodeArea;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class EditorTabPaneService implements PluginEditorTabPaneService {
    private void resyncCodeAreaIds() {
        UIState uiState = UIState.instance;

        for (int i = 0; i < uiState.getEditorTabPane().getTabs().size(); i++) {
            if (((VBox) uiState.getEditorTabPane().getTabs().get(i).getContent()).getChildren().getLast() instanceof CodeArea c) {
                c.setId(String.valueOf(i));
            }
        }
    }

    public void addTabToPane(File file) {
        UIState uiState = UIState.instance;
        ServiceState serviceState = ServiceState.instance;

        addTabToPane(file.getName());

        Tab tab = uiState.getEditorTabPane().getSelectionModel().getSelectedItem();

        if (((VBox) tab.getContent()).getChildren().getLast() instanceof CodeArea c) {
            serviceState.getGrammarService().setGrammarToCodeArea(c, file);

            CodeAreaState.IndividualState individualState = CodeAreaState.instance.getIndividualStates().get(Integer.parseInt(c.getId()));
            individualState.setOpenedFile(file);
            individualState.setSaved(true);
            individualState.setJustOpenedFile(true);

            // Detect file encoding
            Charset detectedCharset = detectFileEncoding(file);
            individualState.setCharset(detectedCharset);

            try (BufferedReader bufferedReader = Files.newBufferedReader(file.toPath(), detectedCharset)) {
                String content = bufferedReader.readAllAsString();
                // Strip BOM if present
                if (content.startsWith("\uFEFF")) {
                    content = content.substring(1);
                }
                c.replaceText(content);
                tab.setText(file.getName());
                tab.setGraphic(FileToGraphicMapper.mapFileToGraphic(file));
            } catch (IOException e) {
                uiState.getEditorTabPane().getTabs().removeLast();
                CodeAreaState.instance.getIndividualStates().remove(individualState);
                serviceState.getNotificationService().showNotificationOk("Could not open specified file.");
                throw new RuntimeException();
            }
        }
    }

    /**
     * Detects the encoding of a file by checking for BOM markers and attempting to read with common charsets.
     *
     * @param file the file to detect encoding for
     * @return the detected Charset, or UTF-8 as default
     */
    private Charset detectFileEncoding(File file) {
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());

            // Check for BOM markers
            if (bytes.length >= 4) {
                if (bytes[0] == (byte) 0xFF && bytes[1] == (byte) 0xFE && 
                    bytes[2] == (byte) 0x00 && bytes[3] == (byte) 0x00) {
                    return Charset.forName("UTF-32LE");
                }
                if (bytes[0] == (byte) 0x00 && bytes[1] == (byte) 0x00 && 
                    bytes[2] == (byte) 0xFE && bytes[3] == (byte) 0xFF) {
                    return Charset.forName("UTF-32BE");
                }
            }

            if (bytes.length >= 3) {
                if (bytes[0] == (byte) 0xEF && bytes[1] == (byte) 0xBB && bytes[2] == (byte) 0xBF) {
                    return StandardCharsets.UTF_8;
                }
            }

            if (bytes.length >= 2) {
                if (bytes[0] == (byte) 0xFF && bytes[1] == (byte) 0xFE) {
                    return Charset.forName("UTF-16LE");
                }
                if (bytes[0] == (byte) 0xFE && bytes[1] == (byte) 0xFF) {
                    return Charset.forName("UTF-16BE");
                }
            }

            // Default to UTF-8
            return StandardCharsets.UTF_8;
        } catch (IOException e) {
            return StandardCharsets.UTF_8;
        }
    }

    public void addTabToPane(String tabTitle) {
        UIState uiState = UIState.instance;

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
        UIState uiState = UIState.instance;
        ServiceState serviceState = ServiceState.instance;

        CodeAreaState.IndividualState individualState = CodeAreaState.instance.getIndividualStates().get(uiState.getEditorTabPane().getSelectionModel().getSelectedIndex());

        if (!individualState.isSaved()) {
            boolean repeatPrompt;

            do {
                repeatPrompt = false;

                var pickedOption = serviceState.getNotificationService().showNotificationYesNoCancel("Save file before closing?");

                if (pickedOption.equals(NotificationYesNoCancelOption.CANCEL)) {
                    return RemoveSelectedTabFromPaneOption.CANCELLED;
                } else if (pickedOption.equals(NotificationYesNoCancelOption.YES)) {
                    if (!serviceState.getFileService().saveFile()) {
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

    public void removeOtherTabsFromPane() {
        UIState uiState = UIState.instance;
        ServiceState serviceState = ServiceState.instance;

        while (uiState.getEditorTabPane().getTabs().size() > 1) {
            int indexToDelete;

            if (uiState.getEditorTabPane().getTabs().getFirst().equals(uiState.getEditorTabPane().getSelectionModel().getSelectedItem())) {
                indexToDelete = 1;
            } else {
                indexToDelete = 0;
            }
            var tab = uiState.getEditorTabPane().getTabs().get(indexToDelete);

            CodeAreaState.IndividualState individualState = CodeAreaState.instance.getIndividualStates().get(indexToDelete);

            if (!promptForUnsavedFile(individualState, serviceState)) {
                resyncCodeAreaIds();
                return;
            }

            CodeAreaState.instance.getIndividualStates().remove(individualState);
            uiState.getEditorTabPane().getTabs().remove(tab);

        }

        resyncCodeAreaIds();
    }

    public void removeAllTabsFromPane() {
        UIState uiState = UIState.instance;
        ServiceState serviceState = ServiceState.instance;

        for (int i = uiState.getEditorTabPane().getTabs().size() - 1; i >= 0; i--) {
            var tab = uiState.getEditorTabPane().getTabs().get(i);

            CodeAreaState.IndividualState individualState = CodeAreaState.instance.getIndividualStates().get(i);

            if (!promptForUnsavedFile(individualState, serviceState)) return;

            CodeAreaState.instance.getIndividualStates().remove(individualState);
            uiState.getEditorTabPane().getTabs().remove(tab);
        }

        addTabToPane("Untitled");
    }

    private static boolean promptForUnsavedFile(CodeAreaState.IndividualState individualState, ServiceState serviceState) {
        if (!individualState.isSaved()) {
            boolean repeatPrompt;

            do {
                repeatPrompt = false;

                var pickedOption = serviceState.getNotificationService().showNotificationYesNoCancel("Save file before closing?");

                if (pickedOption.equals(NotificationYesNoCancelOption.CANCEL)) {
                    return false;
                } else if (pickedOption.equals(NotificationYesNoCancelOption.YES)) {
                    if (!serviceState.getFileService().saveFile()) {
                        repeatPrompt = true;
                    }
                }
            } while (repeatPrompt);
        }
        return true;
    }
}
