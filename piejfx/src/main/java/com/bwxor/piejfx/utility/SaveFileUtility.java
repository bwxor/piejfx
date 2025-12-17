package com.bwxor.piejfx.utility;

import com.bwxor.piejfx.state.CodeAreaState;
import com.bwxor.piejfx.state.StageState;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeView;
import javafx.stage.FileChooser;
import org.fxmisc.richtext.CodeArea;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SaveFileUtility {
    /**
     * Saves the content of the CodeArea present at the selected tab. If no file is associated with it, a saveFileAs
     * is triggered.
     *
     * @return true if file has been saved successfully and false otherwise
     */
    public static boolean saveFile(SplitPane horizontalSplitPane, TreeView folderTreeView, SplitPane verticalSplitPane, TabPane editorTabPane, TabPane terminalTabPane, Label titleBarLabel) {
        CodeAreaState.IndividualState state = CodeAreaState.instance.getIndividualStates().get(editorTabPane.getSelectionModel().getSelectedIndex());

        if (state.getOpenedFile() == null) {
            return saveFileAs(horizontalSplitPane, folderTreeView, verticalSplitPane, editorTabPane, terminalTabPane, titleBarLabel);
        } else {
            try (BufferedWriter br = Files.newBufferedWriter(state.getOpenedFile().toPath(), StandardCharsets.UTF_8)) {
                br.write(state.getContent());
            } catch (IOException e) {
                NotificationUtility.showNotificationOk("Error while trying to save the file.");
                throw new RuntimeException(e);
            }

            if (editorTabPane.getSelectionModel().getSelectedItem().getContent() instanceof CodeArea c) {
                GrammarUtility.setGrammarToCodeArea(c, state.getOpenedFile());
                GrammarUtility.resetCodeAreaStyle(c, state);
            }

            state.setSaved(true);
            editorTabPane.getSelectionModel().getSelectedItem().setText(state.getOpenedFile().getName());
            titleBarLabel.setText(state.getOpenedFile().getName());

            FolderTreeViewUtility.showFolderTreeView(horizontalSplitPane, folderTreeView, verticalSplitPane, editorTabPane, terminalTabPane, titleBarLabel);
        }
        return true;
    }

    /**
     * Opens a @ref FileChooser and saves the content of the selected tab into the chosen file.
     *
     * @return true if a file has been chosen and false otherwise
     */
    public static boolean saveFileAs(SplitPane horizontalSplitPane, TreeView folderTreeView, SplitPane verticalSplitPane, TabPane editorTabPane, TabPane terminalTabPane, Label titleBarLabel) {
        CodeAreaState.IndividualState state = CodeAreaState.instance.getIndividualStates().get(editorTabPane.getSelectionModel().getSelectedIndex());

        String filters;

        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(ResourceUtility.getResourceByNameAsStream("config/extension-filters.json")))) {

            filters = bufferedReader.readAllAsString();

            var fileChooser = new FileChooser();

            JSONObject jsonObject = new JSONObject(filters);
            JSONArray arr = jsonObject.getJSONArray("filters");

            for (int i = 0; i < arr.length(); i++) {
                String description = arr.getJSONObject(i).getString("description");
                String extensions = arr.getJSONObject(i).getString("extensions");
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(description, extensions));
            }

            File selectedFile = fileChooser.showSaveDialog(StageState.instance.getStage());

            if (selectedFile != null) {
                state.setOpenedFile(selectedFile);
                return saveFile(horizontalSplitPane, folderTreeView, verticalSplitPane, editorTabPane, terminalTabPane, titleBarLabel);
            }

            return false;
        } catch (IOException e) {
            NotificationUtility.showNotificationOk("Error while trying to save the file.");
            throw new RuntimeException(e);
        }
    }
}