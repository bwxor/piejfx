package com.bwxor.piejfx.dto;

/**
 * DTO created for better data passing between methods inside @ref ThemeBasedSettingsProvider (color schemes for the
 * terminal).
 *
 * @param red
 * @param green
 * @param blue
 */
public record RGB(int red, int green, int blue) {
}
