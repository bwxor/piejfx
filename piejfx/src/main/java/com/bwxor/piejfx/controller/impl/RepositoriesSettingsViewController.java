package com.bwxor.piejfx.controller.impl;

import com.bwxor.piejfx.controller.MovableViewController;
import com.bwxor.piejfx.state.ServiceState;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class RepositoriesSettingsViewController extends MovableViewController {

    @FXML
    private ListView<String> repositoriesListView;

    private final ObservableList<String> repositories = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        repositoriesListView.setItems(repositories);
        // Double-click an entry to open the edit window pre-filled with its URL.
        repositoriesListView.setCellFactory(list -> {
            ListCell<String> cell = new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                }
            };
            cell.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !cell.isEmpty()) {
                    editRepository(cell.getIndex());
                }
            });
            return cell;
        });
    }

    /** Fills the list with the given URLs. Called by the service before the window is shown. */
    public void setRepositories(List<String> urls) {
        repositories.setAll(urls);
    }

    /** Opens the "Add Repository" window and appends the entered URL to the list. */
    @FXML
    public void onAddButtonClick() {
        Stage owner = (Stage) repositoriesListView.getScene().getWindow();
        Optional<String> added = ServiceState.instance.getRepositoriesSettingsViewService()
                .showAddRepositoryView(owner);

        if (added.isEmpty()) {
            return;
        }

        String candidate = added.get();
        if (repositories.contains(candidate)) {
            notify("This URL is already in the list.");
            return;
        }
        repositories.add(candidate);
    }

    /** Opens the "Edit Repository" window for the entry at {@code index} and replaces it with the result. */
    private void editRepository(int index) {
        if (index < 0 || index >= repositories.size()) {
            return;
        }

        String current = repositories.get(index);
        Stage owner = (Stage) repositoriesListView.getScene().getWindow();
        Optional<String> edited = ServiceState.instance.getRepositoriesSettingsViewService()
                .showEditRepositoryView(owner, current);

        if (edited.isEmpty() || edited.get().equals(current)) {
            return;
        }

        String newUrl = edited.get();
        int existingIndex = repositories.indexOf(newUrl);
        if (existingIndex != -1 && existingIndex != index) {
            notify("This URL is already in the list.");
            return;
        }
        repositories.set(index, newUrl);
    }

    @FXML
    public void onRemoveButtonClick() {
        List<String> selected = List.copyOf(repositoriesListView.getSelectionModel().getSelectedItems());

        if (selected.isEmpty()) {
            notify("Select a URL to remove.");
            return;
        }

        repositories.removeAll(selected);
    }

    /** Saves silently; only a failed write is reported. */
    @FXML
    public void onSaveButtonClick() {
        try {
            ServiceState.instance.getRepositoriesConfigService().save(List.copyOf(repositories));
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.close();
        } catch (IOException e) {
            notify("Error while saving repositories: " + e.getMessage());
        }
    }

    private void notify(String text) {
        Stage owner = (Stage) repositoriesListView.getScene().getWindow();
        ServiceState.instance.getNotificationService().showNotificationOk(text, owner);
    }
}
