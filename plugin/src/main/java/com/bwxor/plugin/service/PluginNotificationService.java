package com.bwxor.plugin.service;

import com.bwxor.plugin.type.NotificationYesNoCancelOption;

public interface PluginNotificationService {
    NotificationYesNoCancelOption showNotificationYesNoCancel(String notificationText);
    void showNotificationOk(String notificationText);
}