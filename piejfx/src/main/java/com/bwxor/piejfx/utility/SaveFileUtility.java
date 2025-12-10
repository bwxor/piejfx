package com.bwxor.piejfx.utility;

import com.bwxor.piejfx.state.CodeAreaState;
import com.bwxor.piejfx.state.StageState;
import javafx.scene.control.TabPane;
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
     * @param tabPane
     * @return true if file has been saved successfully and false otherwise
     */
    public static boolean saveFile(TabPane tabPane) {
        CodeAreaState.IndividualState state = CodeAreaState.instance.getIndividualStates().get(tabPane.getSelectionModel().getSelectedIndex());

        if (state.getOpenedFile() == null) {
            return saveFileAs(tabPane);
        } else {
            try (BufferedWriter br = Files.newBufferedWriter(state.getOpenedFile().toPath(), StandardCharsets.UTF_8)) {
                br.write(state.getContent());
            } catch (IOException e) {
                // ToDo: Show an error
                throw new RuntimeException(e);
            }

            if (tabPane.getSelectionModel().getSelectedItem().getContent() instanceof CodeArea c) {
                GrammarUtility.setGrammarToCodeArea(c, state.getOpenedFile());
                GrammarUtility.resetCodeAreaStyle(c, state);
            }

            state.setSaved(true);
            tabPane.getSelectionModel().getSelectedItem().setText(state.getOpenedFile().getName());
        }

        return true;
    }

    /**
     * Opens a @ref FileChooser and saves the content of the selected tab into the chosen file.
     *
     * @param tabPane
     * @return true if a file has been chosen and false otherwise
     */
    public static boolean saveFileAs(TabPane tabPane) {
        CodeAreaState.IndividualState state = CodeAreaState.instance.getIndividualStates().get(tabPane.getSelectionModel().getSelectedIndex());

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
                return saveFile(tabPane);
            }

            return false;
        } catch (IOException e) {
            // ToDo: Error here
            throw new RuntimeException(e);
        }
    }
}