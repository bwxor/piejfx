package com.bwxor.piejfx.utility;

import com.bwxor.piejfx.factory.TabFactory;
import com.bwxor.piejfx.state.CodeAreaState;
import com.bwxor.piejfx.type.NotificationYesNoCancelOption;
import com.bwxor.piejfx.type.RemoveSelectedTabFromPaneResponse;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.fxmisc.richtext.CodeArea;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class TabPaneUtility {
    private static void resyncCodeAreaIds(TabPane tabPane) {
        for (int i = 0; i < tabPane.getTabs().size(); i++) {
            if (tabPane.getTabs().get(i).getContent() instanceof CodeArea c) {
                c.setId(String.valueOf(i));
            }
        }
    }

    public static void addTabToPane(TabPane tabPane, File file) {
        Tab tab = TabFactory.createEditorTab(tabPane, file.getName());
        tab.setOnCloseRequest(e -> {
            removeSelectedTabFromPane(tabPane);
            e.consume();
        });
        tabPane.getTabs().add(tab);

        resyncCodeAreaIds(tabPane);

        if (tab.getContent() instanceof CodeArea c) {

            GrammarUtility.setGrammarToCodeArea(c, file);

            CodeAreaState.IndividualState individualState = CodeAreaState.instance.getIndividualStates().get(Integer.parseInt(c.getId()));
            individualState.setOpenedFile(file);
            individualState.setSaved(true);

            try (BufferedReader bufferedReader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
                c.replaceText(bufferedReader.readAllAsString());
                tab.setText(tab.getText().substring(1));
            } catch (IOException e) {
                // ToDo: Show an error
                throw new RuntimeException(e);
            }
        }

        tabPane.getSelectionModel().select(tab);

    }

    public static void addTabToPane(TabPane tabPane, String tabTitle) {
        Tab tab = TabFactory.createEditorTab(tabPane, tabTitle);
        tab.setOnCloseRequest(e -> {
            removeSelectedTabFromPane(tabPane);
            e.consume();
        });
        tabPane.getTabs().add(tab);

        resyncCodeAreaIds(tabPane);

        tabPane.getSelectionModel().select(tab);
    }

    /**
     * Removes the selected tab from the pane and prompts to a save if the content is not stored in any file.
     *
     * @param tabPane
     * @return a negative response only if the user was prompted for a save and cancelled it.
     */
    public static RemoveSelectedTabFromPaneResponse removeSelectedTabFromPane(TabPane tabPane) {
        CodeAreaState.IndividualState individualState = CodeAreaState.instance.getIndividualStates().get(tabPane.getSelectionModel().getSelectedIndex());

        if (!individualState.isSaved()) {
            boolean repeatPrompt;

            do {
                repeatPrompt = false;

                var pickedOption = NotificationUtility.showNotificationYesNoCancel("Save file before closing?");

                if (pickedOption.equals(NotificationYesNoCancelOption.CANCEL)) {
                    return RemoveSelectedTabFromPaneResponse.CANCELLED;
                } else if (pickedOption.equals(NotificationYesNoCancelOption.YES)) {
                    if (!SaveFileUtility.saveFile(tabPane)) {
                        repeatPrompt = true;
                    }
                }
            } while (repeatPrompt);
        }

        CodeAreaState.instance.getIndividualStates().remove(individualState);
        tabPane.getTabs().remove(tabPane.getSelectionModel().getSelectedItem());

        if (tabPane.getTabs().isEmpty()) {
            addTabToPane(tabPane, "Untitled");
        }

        resyncCodeAreaIds(tabPane);

        return RemoveSelectedTabFromPaneResponse.REMOVED;
    }
}
