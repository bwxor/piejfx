package com.bwxor.piejfx.service;

import com.bwxor.piejfx.state.CodeAreaState;
import com.bwxor.piejfx.state.ServiceState;
import com.bwxor.piejfx.state.StageState;
import com.bwxor.piejfx.state.UIState;
import javafx.stage.FileChooser;
import org.fxmisc.richtext.CodeArea;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class SaveFileService {
    /**
     * Saves the content of the CodeArea present at the selected tab. If no file is associated with it, a saveFileAs
     * is triggered.
     *
     * @return true if file has been saved successfully and false otherwise
     */
    public boolean saveFile() {
        UIState uiState = UIState.getInstance();
        ServiceState serviceState = ServiceState.getInstance();
        CodeAreaState.IndividualState codeAreaState = CodeAreaState.instance.getIndividualStates().get(uiState.getEditorTabPane().getSelectionModel().getSelectedIndex());

        if (codeAreaState.getOpenedFile() == null) {
            return saveFileAs();
        } else {
            try (BufferedWriter br = Files.newBufferedWriter(codeAreaState.getOpenedFile().toPath(), StandardCharsets.UTF_8)) {
                br.write(codeAreaState.getContent());
            } catch (IOException e) {
                serviceState.getNotificationService().showNotificationOk("Error while trying to save the file.");
                throw new RuntimeException(e);
            }

            if (uiState.getEditorTabPane().getSelectionModel().getSelectedItem().getContent() instanceof CodeArea c) {
                serviceState.getGrammarService().setGrammarToCodeArea(c, codeAreaState.getOpenedFile());
                serviceState.getGrammarService().resetCodeAreaStyle(c, codeAreaState);
            }

            codeAreaState.setSaved(true);
            uiState.getEditorTabPane().getSelectionModel().getSelectedItem().setText(codeAreaState.getOpenedFile().getName());
            uiState.getTitleBarLabel().setText(codeAreaState.getOpenedFile().getName());

            serviceState.getFolderTreeViewService().showFolderTreeView();
        }
        return true;
    }

    /**
     * Opens a @ref FileChooser and saves the content of the selected tab into the chosen file.
     *
     * @return true if a file has been chosen and false otherwise
     */
    public boolean saveFileAs() {
        UIState uiState = UIState.getInstance();
        ServiceState serviceState = ServiceState.getInstance();

        CodeAreaState.IndividualState codeAreaState = CodeAreaState.instance.getIndividualStates().get(uiState.getEditorTabPane().getSelectionModel().getSelectedIndex());

        String filters;

        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(serviceState.getResourceService().getResourceByNameAsStream("config/extension-filters.json")))) {

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
                codeAreaState.setOpenedFile(selectedFile);
                return saveFile();
            }

            return false;
        } catch (IOException e) {
            serviceState.getNotificationService().showNotificationOk("Error while trying to save the file.");
            throw new RuntimeException(e);
        }
    }
}