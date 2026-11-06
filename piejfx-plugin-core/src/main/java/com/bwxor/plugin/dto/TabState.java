package com.bwxor.plugin.dto;

import java.io.File;

public record TabState(String tabName, File openedFile) {
}
