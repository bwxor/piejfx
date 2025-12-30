package com.bwxor.plugin.service;

import com.bwxor.plugin.dto.NewFileResponse;

import java.io.File;

public interface PluginFileOperationsService {
    NewFileResponse showNewFileWindow(String title);
    boolean deleteFolder(File file);
}