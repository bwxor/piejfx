package com.bwxor.plugin.service;

import java.io.File;

public interface PluginFileOperationsService {
    NewFileResponse showNewFileWindow(String title);
    boolean deleteFolder(File file);
}