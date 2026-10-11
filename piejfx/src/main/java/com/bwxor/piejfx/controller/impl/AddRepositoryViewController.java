package com.bwxor.piejfx.controller.impl;

import com.bwxor.piejfx.controller.MovableViewController;
import com.bwxor.piejfx.service.RepositoriesConfigService;
import com.bwxor.piejfx.state.ServiceState;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Small modal window that asks for a single repository URL. Used both to add a new URL
 * and to edit an existing one. The result is only available through {@link #getResult()}
 * after the window has closed; it is null when the user cancelled.
 */
public class AddRepositoryViewController extends MovableViewController {

    @FXML
    private Label windowTitleLabel;
    @FXML
    private TextField urlTextField;
    @FXML
    private Button addButton;

    private String result;

    public void initialize() {
        Platform.runLater(() -> urlTextField.requestFocus());
    }

    /**
     * Adapts the window to its purpose and pre-fills the textbox.
     *
     * @param title      window title, e.g. "Add Repository" or "Edit Repository"
     * @param confirmText label of the confirm button, e.g. "Add" or "Update"
     * @param initialUrl URL to pre-fill, or null for an empty textbox
     */
    public void configure(String title, String confirmText, String initialUrl) {
        windowTitleLabel.setText(title);
        addButton.setText(confirmText);
        urlTextField.setText(initialUrl == null ? "" : initialUrl);
        urlTextField.positionCaret(urlTextField.getText().length());
    }

    /** The validated URL entered by the user, or null if the window was cancelled/closed. */
    public String getResult() {
        return result;
    }

    @FXML
    public void onAddButtonClick() {
        String candidate = urlTextField.getText().trim();

        if (candidate.isEmpty()) {
            notify("Please enter a URL.");
            return;
        }
        if (!RepositoriesConfigService.isValidUrl(candidate)) {
            notify("'" + candidate + "' is not a valid URL. It must start with http:// or https://.");
            return;
        }

        result = candidate;
        close();
    }

    @FXML
    public void onCancelButtonClick() {
        result = null;
        close();
    }

    @Override
    @FXML
    public void onCloseButtonClick() {
        onCancelButtonClick();
    }

    private void close() {
        ((Stage) urlTextField.getScene().getWindow()).close();
    }

    private void notify(String text) {
        Stage owner = (Stage) urlTextField.getScene().getWindow();
        ServiceState.instance.getNotificationService().showNotificationOk(text, owner);
    }
}
