package com.bwxor.piejfx.factory;

import com.bwxor.piejfx.connector.LocalPtyProcessTtyConnector;
import com.bwxor.piejfx.provider.ThemeBasedSettingsProvider;
import com.bwxor.piejfx.state.CodeAreaState;
import com.bwxor.piejfx.state.TerminalState;
import com.pty4j.PtyProcess;
import com.pty4j.PtyProcessBuilder;
import com.techsenger.jeditermfx.core.util.Platform;
import com.techsenger.jeditermfx.ui.DefaultHyperlinkFilter;
import com.techsenger.jeditermfx.ui.JediTermFxWidget;
import com.bwxor.piejfx.dto.CreateTtyConnectorResponse;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import javafx.scene.input.ScrollEvent;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class TabFactory {
    private static final String EMPTY_STRING = "";
    public static Tab createEditorTab() {
        return createEditorTab(EMPTY_STRING);
    }

    public static Tab createEditorTab(String title) {
        Tab tab = new Tab();

        CodeArea codeArea = new CodeArea();
        codeArea.setStyle(String.format("-fx-font-size: %dpt", 10));

        CodeAreaState.IndividualState createdState = new CodeAreaState.IndividualState();
        createdState.setSaved(true);
        CodeAreaState.instance.getIndividualStates().add(createdState);

        codeArea.setOnKeyPressed(e -> {
            if (e.isControlDown()) {
                CodeAreaState.IndividualState individualState = CodeAreaState.instance.getIndividualStates().get(Integer.parseInt(codeArea.getId()));

                if (e.getCode().equals(KeyCode.ADD)) {
                    individualState.setFontSize(individualState.getFontSize() + 2);
                    codeArea.setStyle(String.format("-fx-font-size: %dpt", individualState.getFontSize()));
                }
                else if (e.getCode().equals(KeyCode.SUBTRACT)) {
                    individualState.setFontSize(individualState.getFontSize() - 2);
                    codeArea.setStyle(String.format("-fx-font-size: %dpt", individualState.getFontSize()));
                }
            }
        });

        codeArea.addEventFilter(ScrollEvent.SCROLL,
                e -> {
                    if (e.isControlDown()) {
                        CodeAreaState.IndividualState individualState = CodeAreaState.instance.getIndividualStates().get(Integer.parseInt(codeArea.getId()));

                        if (e.getDeltaY() > 0) {
                            individualState.setFontSize(individualState.getFontSize() + 2);
                            codeArea.setStyle(String.format("-fx-font-size: %dpt", individualState.getFontSize()));
                        }
                        else {
                            individualState.setFontSize(individualState.getFontSize() - 2);
                            codeArea.setStyle(String.format("-fx-font-size: %dpt", individualState.getFontSize()));
                        }
                    }
                }
        );

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
            }
            else {
                command = new String[] {cmdToRun};
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
