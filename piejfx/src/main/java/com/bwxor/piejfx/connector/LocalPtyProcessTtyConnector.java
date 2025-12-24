package com.bwxor.piejfx.connector;

import com.pty4j.PtyProcess;
import com.techsenger.jeditermfx.core.ProcessTtyConnector;

import java.nio.charset.Charset;

/**
 * Custom TtyConnector for the JediTermFxWidget Terminal.
 */
public class LocalPtyProcessTtyConnector extends ProcessTtyConnector {
    private final PtyProcess myProcess;

    public LocalPtyProcessTtyConnector(PtyProcess process, Charset charset) {
        super(process, charset);
        this.myProcess = process;
    }

    @Override
    public String getName() {
        return "Local Terminal";
    }
}