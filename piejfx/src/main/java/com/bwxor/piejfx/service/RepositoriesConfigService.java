package com.bwxor.piejfx.service;

import com.bwxor.piejfx.constants.AppDirConstants;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Manages the repositories.json file in the config directory.
 * Format: a JSON array of plugin repository URLs, e.g. ["https://example.com/plugins.json"].
 */
public class RepositoriesConfigService {

    public static final String DEFAULT_REPOSITORY = "https://bwxor.github.io/metadata/piejfx-plugins.json";

    /**
     * Creates repositories.json with the default repository if it does not exist yet
     * (e.g. for installations whose config folder was created before this file existed).
     */
    public void ensureFileExists() {
        if (!AppDirConstants.REPOSITORIES_FILE.toFile().exists()) {
            try {
                save(List.of(DEFAULT_REPOSITORY));
            } catch (IOException e) {
                throw new RuntimeException("Failed to create repositories.json", e);
            }
        }
    }

    /**
     * Reads the repository URLs from repositories.json. Invalid or duplicate entries are skipped.
     */
    public List<String> load() throws IOException {
        String content = Files.readString(AppDirConstants.REPOSITORIES_FILE);

        JSONArray array;
        try {
            array = new JSONArray(content);
        } catch (JSONException e) {
            throw new IOException("repositories.json is not a valid JSON array.", e);
        }

        Set<String> urls = new LinkedHashSet<>();
        for (int i = 0; i < array.length(); i++) {
            String candidate = array.optString(i, "").trim();
            if (isValidUrl(candidate)) {
                urls.add(candidate);
            }
        }
        return new ArrayList<>(urls);
    }

    /**
     * Writes the given repository URLs to repositories.json, replacing its previous content.
     */
    public void save(List<String> urls) throws IOException {
        JSONArray array = new JSONArray();
        urls.forEach(array::put);

        AppDirConstants.REPOSITORIES_FILE.toFile().getParentFile().mkdirs();
        Files.writeString(AppDirConstants.REPOSITORIES_FILE, array.toString(2));
    }

    /**
     * A repository URL is valid when it is an absolute http or https URI with a host.
     */
    public static boolean isValidUrl(String candidate) {
        if (candidate == null || candidate.isBlank()) {
            return false;
        }
        try {
            URI uri = new URI(candidate.trim());
            String scheme = uri.getScheme();
            return ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme))
                    && uri.getHost() != null;
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
