package com.bwxor.plugin.service;

public interface PluginNotificationService {
    NotificationYesNoCancelOption showNotificationYesNoCancel(String notificationText);
    void showNotificationOk(String notificationText);
}