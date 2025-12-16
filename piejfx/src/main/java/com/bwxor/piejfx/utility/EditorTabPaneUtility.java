package com.bwxor.piejfx.utility;

import com.bwxor.piejfx.factory.TabFactory;
import com.bwxor.piejfx.state.CodeAreaState;
import com.bwxor.piejfx.type.NotificationYesNoCancelOption;
import com.bwxor.piejfx.type.RemoveSelectedTabFromPaneResponse;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.fxmisc.richtext.CodeArea;

import javax.management.Notification;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class EditorTabPaneUtility {
    private static void resyncCodeAreaIds(TabPane tabPane) {
        for (int i = 0; i < tabPane.getTabs().size(); i++) {
            if (tabPane.getTabs().get(i).getContent() instanceof CodeArea c) {
                c.setId(String.valueOf(i));
            }
        }
    }

    public static void addTabToPane(SplitPane verticalSplitPane, TabPane editorTabPane, TabPane terminalTabPane, Label titleBarLabel, File file) {
        Tab tab = TabFactory.createEditorTab(verticalSplitPane, terminalTabPane, file.getName());
        tab.setOnCloseRequest(e -> {
            removeSelectedTabFromPane(verticalSplitPane, editorTabPane, terminalTabPane, titleBarLabel);
            e.consume();
        });
        editorTabPane.getTabs().add(tab);

        resyncCodeAreaIds(editorTabPane);

        if (tab.getContent() instanceof CodeArea c) {
            GrammarUtility.setGrammarToCodeArea(c, file);

            CodeAreaState.IndividualState individualState = CodeAreaState.instance.getIndividualStates().get(Integer.parseInt(c.getId()));
            individualState.setOpenedFile(file);
            individualState.setSaved(true);

            try (BufferedReader bufferedReader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
                c.replaceText(bufferedReader.readAllAsString());
                tab.setText(tab.getText().substring(1));
            } catch (IOException e) {
                editorTabPane.getTabs().remove(editorTabPane.getTabs().size() - 1);
                NotificationUtility.showNotificationOk("Could not open specified file.");
            }
        }

        editorTabPane.getSelectionModel().select(tab);

    }

    public static void addTabToPane(SplitPane verticalSplitPane, TabPane editorTabPane, TabPane terminalTabPane, String tabTitle, Label titleBarLabel) {
        Tab tab = TabFactory.createEditorTab(verticalSplitPane, terminalTabPane, tabTitle);
        tab.setOnCloseRequest(e -> {
            removeSelectedTabFromPane(verticalSplitPane, editorTabPane, terminalTabPane, titleBarLabel);
            e.consume();
        });
        editorTabPane.getTabs().add(tab);

        resyncCodeAreaIds(editorTabPane);

        editorTabPane.getSelectionModel().select(tab);
    }

    /**
     * Removes the selected tab from the pane and prompts to a save if the content is not stored in any file.
     *
     * @param verticalSplitPane
     * @param editorTabPane
     * @return a negative response only if the user was prompted for a save and cancelled it.
     */
    public static RemoveSelectedTabFromPaneResponse removeSelectedTabFromPane(SplitPane verticalSplitPane, TabPane editorTabPane, TabPane terminalTabPane, Label titleBarLabel) {
        CodeAreaState.IndividualState individualState = CodeAreaState.instance.getIndividualStates().get(editorTabPane.getSelectionModel().getSelectedIndex());

        if (!individualState.isSaved()) {
            boolean repeatPrompt;

            do {
                repeatPrompt = false;

                var pickedOption = NotificationUtility.showNotificationYesNoCancel("Save file before closing?");

                if (pickedOption.equals(NotificationYesNoCancelOption.CANCEL)) {
                    return RemoveSelectedTabFromPaneResponse.CANCELLED;
                } else if (pickedOption.equals(NotificationYesNoCancelOption.YES)) {
                    if (!SaveFileUtility.saveFile(editorTabPane, titleBarLabel)) {
                        repeatPrompt = true;
                    }
                }
            } while (repeatPrompt);
        }

        CodeAreaState.instance.getIndividualStates().remove(individualState);
        editorTabPane.getTabs().remove(editorTabPane.getSelectionModel().getSelectedItem());

        if (editorTabPane.getTabs().isEmpty()) {
            addTabToPane(verticalSplitPane, editorTabPane, terminalTabPane, "Untitled", titleBarLabel);
        }

        resyncCodeAreaIds(editorTabPane);

        return RemoveSelectedTabFromPaneResponse.REMOVED;
    }
}
