package com.bwxor.piejfx.factory;

import com.bwxor.piejfx.connector.LocalPtyProcessTtyConnector;
import com.bwxor.piejfx.listener.AutofillChangeListener;
import com.bwxor.piejfx.provider.ThemeBasedSettingsProvider;
import com.bwxor.piejfx.state.CodeAreaState;
import com.bwxor.piejfx.state.ServiceState;
import com.bwxor.piejfx.state.TerminalState;
import com.bwxor.piejfx.state.UIState;
import com.bwxor.piejfx.state.WordWrapState;
import com.pty4j.PtyProcess;
import com.pty4j.PtyProcessBuilder;
import java.util.concurrent.atomic.AtomicInteger;
import com.techsenger.jeditermfx.core.CursorShape;
import com.techsenger.jeditermfx.core.util.Platform;
import com.techsenger.jeditermfx.ui.DefaultHyperlinkFilter;
import com.techsenger.jeditermfx.ui.JediTermFxWidget;
import com.bwxor.piejfx.dto.CreateTtyConnectorResponse;
import javafx.event.Event;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class TabFactory {
    private static final String TAB_REPLACEMENT = "    ";

    public static Tab createEditorTab(String title) {
        ServiceState serviceState = ServiceState.instance;
        UIState uiState = UIState.instance;

        Tab tab = new Tab();

        CodeArea codeArea = new CodeArea();
        codeArea.setStyle(String.format(
                "-fx-font-size: %dpt; " +
                        "-fx-font-family: 'JetBrains Mono'; " +
                        "-fx-font-feature-settings: 'liga' 1, 'calt' 1;",
                10
        ));

        // Apply word wrap setting from configuration
        codeArea.setWrapText(WordWrapState.instance.isWordWrapEnabled());

        var changeListener = new AutofillChangeListener(codeArea);
        changeListener.setOpenPopup(false);
        codeArea.textProperty().addListener(changeListener);

        CodeAreaState.IndividualState createdState = new CodeAreaState.IndividualState();
        createdState.setSaved(true);
        CodeAreaState.instance.getIndividualStates().add(createdState);

        codeArea.setContextMenu(
                ContextMenuFactory.createCodeAreaContextMenu()
        );

        AtomicInteger textChanges = new AtomicInteger();
        codeArea.setOnKeyPressed(e -> {
            int changesBefore = textChanges.get();
            serviceState.getPluginService().invokeOnKeyPress(e);
            if (textChanges.get() != changesBefore) {
                // A plugin changed the text: restyle now, like a save does, instead of waiting for the debounced highlight.
                CodeAreaState.IndividualState restyleState = CodeAreaState.instance.getIndividualStates().get(Integer.parseInt(codeArea.getId()));
                if (restyleState.getGrammar() != null) {
                    serviceState.getGrammarService().resetCodeAreaStyle(codeArea, restyleState);
                }
            }

            if (e.isControlDown()) {
                CodeAreaState.IndividualState individualState = CodeAreaState.instance.getIndividualStates().get(Integer.parseInt(codeArea.getId()));

                if (e.getCode().equals(KeyCode.ADD)) {
                    individualState.setFontSize(individualState.getFontSize() + 2);
                } else if (e.getCode().equals(KeyCode.SUBTRACT)) {
                    individualState.setFontSize(individualState.getFontSize() - 2);
                }

                codeArea.setStyle(String.format(
                        "-fx-font-size: %dpt; " +
                                "-fx-font-family: 'JetBrains Mono'; " +
                                "-fx-font-feature-settings: 'liga' 1, 'calt' 1;",
                        individualState.getFontSize()
                ));

            }

            uiState.getNumCharsLabel().setText(codeArea.getText().length() + " chars");
        });

        codeArea.caretPositionProperty().addListener((obs, oldPos, newPos) -> {
            uiState.getLineColLabel().setText((codeArea.getCurrentParagraph() + 1) + "::" +  (codeArea.getCaretColumn() + 1));
        });

        codeArea.addEventFilter(ScrollEvent.SCROLL,
                e -> {
                    if (e.isControlDown()) {
                        CodeAreaState.IndividualState individualState = CodeAreaState.instance.getIndividualStates().get(Integer.parseInt(codeArea.getId()));

                        if (e.getDeltaY() > 0) {
                            individualState.setFontSize(individualState.getFontSize() + 2);
                        } else {
                            individualState.setFontSize(individualState.getFontSize() - 2);
                        }

                        codeArea.setStyle(String.format(
                                "-fx-font-size: %dpt; " +
                                        "-fx-font-family: 'JetBrains Mono'; " +
                                        "-fx-font-feature-settings: 'liga' 1, 'calt' 1;",
                                individualState.getFontSize()
                        ));
                    }
                }
        );

        // Indentation
        codeArea.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            String text = codeArea.getText();
            int caretPosition = codeArea.getCaretPosition();

            if (e.getCode().equals(KeyCode.TAB)) {
                if (e.isShiftDown() && caretPosition > 0) {
                    var trailingWhitespaceStats = serviceState.getParsingService().getTrailingWhitespaceStatisticsForRemoval(text, caretPosition);

                    codeArea.moveTo(trailingWhitespaceStats.firstIndexAfterConsecutiveWhitespaces());
                    int currentPos = codeArea.getCaretPosition();

                    if (trailingWhitespaceStats.tab()) {
                        codeArea.deleteText(currentPos - 1, currentPos);
                    }
                    else if (trailingWhitespaceStats.consecutiveSpaces() > 0) {
                        int spacesToDelete = trailingWhitespaceStats.consecutiveSpaces() % 4;
                        if (spacesToDelete == 0) {
                            spacesToDelete = Math.min(4, trailingWhitespaceStats.consecutiveSpaces());
                        }
                        codeArea.deleteText(currentPos - spacesToDelete, currentPos);
                    }
                }
                else {
                    codeArea.insertText(caretPosition, TAB_REPLACEMENT);
                }
                e.consume();
            } else if (e.getCode().equals(KeyCode.ENTER)) {
                int noWhitespaces = serviceState.getParsingService().countWhitespacesOnCurrentLineAfterCaret(text, caretPosition);

                if (caretPosition > 0 && (caretPosition == text.length() || noWhitespaces >= 0)) {
                    String spacesToAppend = serviceState.getParsingService().getSpacesToAppendForNewLineIndentation(text, caretPosition);

                    int insertAt = caretPosition + noWhitespaces;
                    codeArea.replaceText(insertAt, insertAt, spacesToAppend);
                    codeArea.moveTo(insertAt + spacesToAppend.length());
                    e.consume();
                }
            }
        });

        // Hotkeys
        codeArea.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.isControlDown()) {
                if (e.getCode().equals(KeyCode.X)) {
                    IndexRange indexRange = codeArea.getSelection();
                    if (indexRange.getLength() == 0) {
                        e.consume();

                        codeArea.selectLine();
                        codeArea.cut();
                        codeArea.deleteNextChar();
                    }
                } else if (e.getCode().equals(KeyCode.C)) {
                    IndexRange indexRange = codeArea.getSelection();
                    if (indexRange.getLength() == 0) {
                        e.consume();

                        codeArea.selectLine();
                        codeArea.copy();
                    }
                } else if (e.getCode().equals(KeyCode.D)) {
                    codeArea.selectLine();
                    String text = codeArea.getSelectedText();
                    codeArea.replaceText(codeArea.getSelection().getEnd(), codeArea.getSelection().getEnd(), System.lineSeparator() + text);
                }
            }
        });

        codeArea.plainTextChanges().subscribe(
                _ -> {
                    CodeAreaState.IndividualState individualState = CodeAreaState.instance.getIndividualStates().get(Integer.parseInt(codeArea.getId()));
                    individualState.setContent(codeArea.getText());
                    textChanges.incrementAndGet();

                    if (individualState.isSaved()) {
                        if (!individualState.isJustOpenedFile()) {
                            individualState.setSaved(false);
                            tab.setText("*" + tab.getText());
                        }

                        individualState.setJustOpenedFile(false);
                    }
                }
        );

        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        tab.setText(title);
        VBox vBox = new VBox();
        vBox.getChildren().add(codeArea);
        VBox.setVgrow(codeArea, Priority.ALWAYS);
        tab.setContent(vBox);
        return tab;
    }

    public static Tab createTerminalTab(String cmdToRun) {
        Tab tab = new Tab();

        JediTermFxWidget jediTermFxWidget = new JediTermFxWidget(80, 24, new ThemeBasedSettingsProvider());
        var createTtyConnectorResponse = createTtyConnector(cmdToRun);
        jediTermFxWidget.setTtyConnector(createTtyConnectorResponse.ttyConnector());
        jediTermFxWidget.addHyperlinkFilter(new DefaultHyperlinkFilter());
        jediTermFxWidget.getPane().setOnContextMenuRequested(
                Event::consume
        );
        jediTermFxWidget.getTerminalPanel().setCursorShape(CursorShape.BLINK_VERTICAL_BAR);
        jediTermFxWidget.start();

        TerminalState.instance.getTerminals().add(jediTermFxWidget);

        tab.setText(createTtyConnectorResponse.commandName());
        tab.setContent(jediTermFxWidget.getPane());
        return tab;
    }

    private static CreateTtyConnectorResponse createTtyConnector(String cmdToRun) {
        try {
            Map<String, String> envs = System.getenv();
            String[] command;

            if (cmdToRun == null || cmdToRun.isEmpty()) {
                if (Platform.isWindows()) {
                    command = new String[]{"cmd.exe"};
                } else {
                    command = new String[]{"/bin/bash", "--login"};
                    envs = new HashMap<>(System.getenv());
                    envs.put("TERM", "xterm-256color");
                }
            } else {
                command = cmdToRun.split("\u0000");
            }

            PtyProcess process = new PtyProcessBuilder().setCommand(command).setEnvironment(envs).start();
            return new CreateTtyConnectorResponse(
                    new LocalPtyProcessTtyConnector(process, StandardCharsets.UTF_8),
                    command[0]);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
