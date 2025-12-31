package com.bwxor.piejfx.factory;

import com.bwxor.piejfx.connector.LocalPtyProcessTtyConnector;
import com.bwxor.piejfx.listener.AutofillChangeListener;
import com.bwxor.piejfx.provider.ThemeBasedSettingsProvider;
import com.bwxor.piejfx.state.CodeAreaState;
import com.bwxor.piejfx.state.ServiceState;
import com.bwxor.piejfx.state.TerminalState;
import com.pty4j.PtyProcess;
import com.pty4j.PtyProcessBuilder;
import com.techsenger.jeditermfx.core.util.Platform;
import com.techsenger.jeditermfx.ui.DefaultHyperlinkFilter;
import com.techsenger.jeditermfx.ui.JediTermFxWidget;
import com.bwxor.piejfx.dto.CreateTtyConnectorResponse;
import javafx.event.Event;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class TabFactory {
    public static Tab createEditorTab(String title) {
        ServiceState serviceState = ServiceState.getInstance();

        Tab tab = new Tab();

        CodeArea codeArea = new CodeArea();
        codeArea.setStyle(String.format(
                "-fx-font-size: %dpt; " +
                        "-fx-font-family: 'JetBrains Mono'; " +
                        "-fx-font-feature-settings: 'liga' 1, 'calt' 1;",
                10
        ));

        var changeListener = new AutofillChangeListener(codeArea);
        changeListener.setOpenPopup(false);
        codeArea.textProperty().addListener(changeListener);


        CodeAreaState.IndividualState createdState = new CodeAreaState.IndividualState();
        createdState.setSaved(true);
        CodeAreaState.instance.getIndividualStates().add(createdState);

        codeArea.setContextMenu(
                ContextMenuFactory.createCodeAreaContextMenu()
        );

        codeArea.setOnKeyPressed(e -> {
            serviceState.getPluginService().invokeOnKeyPress(e);

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
                }
                else if (e.getCode().equals(KeyCode.D)) {
                    codeArea.selectLine();
                    String text = codeArea.getSelectedText();
                    codeArea.replaceText(codeArea.getSelection().getEnd(), codeArea.getSelection().getEnd(), System.lineSeparator() + text);
                }
            }
        });

        codeArea.plainTextChanges().subscribe(
                e -> {
                    CodeAreaState.IndividualState individualState = CodeAreaState.instance.getIndividualStates().get(Integer.parseInt(codeArea.getId()));
                    individualState.setContent(codeArea.getText());

                    if (individualState.isSaved()) {
                        individualState.setSaved(false);
                        tab.setText("*" + tab.getText());
                    }
                }
        );

        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        tab.setText(title);
        tab.setContent(codeArea);
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
                command = new String[]{cmdToRun};
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
