package com.bwxor.plugin.dto;


import com.bwxor.plugin.type.NewFileOption;

/**
 * The response returned by the @ref NewFileController when creating a new file from the folder navigation view. A
 * "New File" window is created by the @ref FileOperationsUtility and the user's inputs are mapped to the response.
 *
 * @param option   Either CREATE, if the user clicked on the "Create" button inside the new dialog, or CANCEL otherwise
 * @param fileName The file name, written by the user in the TextField of the new dialog
 */
public record NewFileResponse(NewFileOption option, String fileName) {
}
