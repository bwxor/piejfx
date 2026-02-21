package com.bwxor.plugin.service;

import com.bwxor.plugin.dto.NewFileResponse;

import java.io.File;

public interface PluginFileService {
    void openFile();
    void openFile(File file);
    NewFileResponse showNewFileWindow(String title);
    boolean deleteFolder(File file);
    void openFolder(File file);
}