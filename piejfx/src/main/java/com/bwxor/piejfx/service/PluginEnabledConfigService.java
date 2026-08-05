package com.bwxor.piejfx.service;

import com.bwxor.piejfx.constants.AppDirConstants;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Manages the plugins-enabled.json file in the user data directory.
 * Format: [{"slug": "plugin-slug", "enabled": true}, ...]
 *
 * The file is read once on {@link #ensureFileExists()} and all subsequent
 * queries are served from an in-memory map. Writes only happen when the map
 * changes (install, uninstall, enable/disable).
 */
public class PluginEnabledConfigService {

    /** In-memory mirror of the file. Populated once by {@link #ensureFileExists()}. */
    private final Map<String, Boolean> cache = new LinkedHashMap<>();

    /**
     * Creates the file with an empty array if it does not already exist, then
     * loads its contents into the in-memory cache. Call once at application start.
     */
    public void ensureFileExists() {
        File file = AppDirConstants.PLUGINS_ENABLED_FILE.toFile();
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                Files.writeString(file.toPath(), "[]");
            } catch (IOException e) {
                throw new RuntimeException("Failed to create plugins-enabled.json", e);
            }
        }
        loadCache();
    }

    /**
     * Returns whether the plugin with the given slug is enabled.
     * If the slug is not yet tracked it is added as enabled, persisted, and
     * {@code true} is returned.
     */
    public boolean isEnabled(String slug) {
        if (cache.containsKey(slug)) {
            return cache.get(slug);
        }
        // New slug discovered at load time — register as enabled
        cache.put(slug, true);
        persist();
        return true;
    }

    /**
     * Adds a new entry (enabled = true) for the slug if it is not already
     * tracked. Used when a plugin is installed.
     */
    public void addIfAbsent(String slug) {
        if (!cache.containsKey(slug)) {
            cache.put(slug, true);
            persist();
        }
    }

    /**
     * Removes the entry for the given slug. Used when a plugin is uninstalled.
     */
    public void remove(String slug) {
        if (cache.remove(slug) != null) {
            persist();
        }
    }

    /**
     * Updates the enabled flag for the given slug. If the slug is not yet
     * tracked it is added. Used when the user clicks Enable/Disable.
     */
    public void setEnabled(String slug, boolean enabled) {
        Boolean current = cache.get(slug);
        if (current == null || current != enabled) {
            cache.put(slug, enabled);
            persist();
        }
    }

    /** Reads the JSON file and populates the cache. */
    private void loadCache() {
        try {
            String content = Files.readString(AppDirConstants.PLUGINS_ENABLED_FILE);
            JSONArray array = new JSONArray(content);
            cache.clear();
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                cache.put(obj.getString("slug"), obj.getBoolean("enabled"));
            }
        } catch (IOException e) {
            // Leave the cache empty; the file will be written clean on the next persist()
        }
    }

    /** Serialises the in-memory cache to disk. */
    private void persist() {
        JSONArray array = new JSONArray();
        cache.forEach((slug, enabled) -> {
            JSONObject entry = new JSONObject();
            entry.put("slug", slug);
            entry.put("enabled", enabled);
            array.put(entry);
        });
        try {
            Files.writeString(AppDirConstants.PLUGINS_ENABLED_FILE, array.toString());
        } catch (IOException e) {
            throw new RuntimeException("Failed to write plugins-enabled.json", e);
        }
    }
}
