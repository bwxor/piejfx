package com.bwxor.plugin.dto;

import com.bwxor.plugin.type.RenameFileOption;

/**
 * The response returned by the @ref RenameFileController when renaming a file from the folder navigation view. A
 * "Rename File" window is created by the @ref FileOperationsUtility and the user's inputs are mapped to the response.
 *
 * @param option  Either RENAME, if the user clicked on the "Rename" button inside the new dialog, or CANCEL otherwise
 * @param oldName The name of the file before the rename operation
 * @param newName The name of the file after the rename operation
 */
public record RenameFileResponse(RenameFileOption option, String oldName, String newName) {
}
