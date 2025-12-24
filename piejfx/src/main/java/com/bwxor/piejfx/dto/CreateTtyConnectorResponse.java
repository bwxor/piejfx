package com.bwxor.piejfx.dto;

import com.techsenger.jeditermfx.core.TtyConnector;

/**
 * Output-DTO used when creating a custom TtyConnector object for the JediTermFxWidget terminal.
 * @param ttyConnector A reference to the created connector
 * @param commandName OS-Specific. This is the main reason why this record is used.
 */
public record CreateTtyConnectorResponse(TtyConnector ttyConnector, String commandName) {

}
