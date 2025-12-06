package com.bwxor.piejfx.utility;

import com.bwxor.piejfx.factory.TabFactory;
import com.bwxor.piejfx.state.CodeAreaState;
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
        tabPane.getTabs().add(tab);

        resyncCodeAreaIds(tabPane);

        if (tab.getContent() instanceof CodeArea c) {
            CodeAreaState.IndividualState individualState = CodeAreaState.instance.getIndividualStates().get(Integer.parseInt(c.getId()));
            individualState.setOpenedFile(file);

            try (BufferedReader bufferedReader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8))
            {
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
        tabPane.getTabs().add(tab);

        resyncCodeAreaIds(tabPane);

        tabPane.getSelectionModel().select(tab);
    }

    public static void removeSelectedTabFromPane(TabPane tabPane) {
        CodeAreaState.instance.getIndividualStates().remove(tabPane.getSelectionModel().getSelectedIndex());
        tabPane.getTabs().remove(tabPane.getSelectionModel().getSelectedItem());

        if (tabPane.getTabs().isEmpty()) {
            addTabToPane(tabPane, "Untitled");
        }

        resyncCodeAreaIds(tabPane);
    }
}
