package com.bwxor.piejfx.utility;

import com.bwxor.piejfx.factory.TabFactory;
import com.bwxor.piejfx.state.CodeAreaState;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.fxmisc.richtext.CodeArea;

public class TabPaneUtility {
    private static void resyncCodeAreaIds(TabPane tabPane) {
        for (int i = 0; i < tabPane.getTabs().size(); i++) {
            if (tabPane.getTabs().get(i).getContent() instanceof CodeArea c) {
                c.setId(String.valueOf(i));
            }
        }
    }

    public static void addTabToPane(TabPane tabPane, String tabTitle) {
        Tab tab = TabFactory.createEditorTab(tabTitle);
        tabPane.getTabs().add(tab);

        resyncCodeAreaIds(tabPane);

        tabPane.getSelectionModel().select(tab);
    }

    public static void removeSelectedTabFromPane(TabPane tabPane) {
        CodeAreaState.instance.getIndividualStates().remove(tabPane.getSelectionModel().getSelectedIndex());
        tabPane.getTabs().remove(tabPane.getSelectionModel().getSelectedItem());

        if (tabPane.getTabs().isEmpty()) {
            addTabToPane(tabPane, "New File");
        }

        resyncCodeAreaIds(tabPane);
    }
}
