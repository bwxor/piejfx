package com.bwxor.piejfx.service;

import com.bwxor.piejfx.dto.FetchedPlugin;
import com.bwxor.piejfx.exception.FetchPluginsServiceException;
import com.bwxor.piejfx.state.ServiceState;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FetchPluginsService {

    /**
     * Fetches the plugin list from the default API and from every repository listed in repositories.json.
     * Plugins are merged and de-duplicated by slug (the first occurrence wins).
     */
    public List<FetchedPlugin> fetchPlugins() throws FetchPluginsServiceException {
        Set<String> sources = new LinkedHashSet<>();
        try {
            sources.addAll(ServiceState.instance.getRepositoriesConfigService().load());
        } catch (IOException e) {
            throw new FetchPluginsServiceException(e);
        }

        Map<String, FetchedPlugin> merged = new LinkedHashMap<>();
        try (HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build()) {
            for (String source : sources) {
                for (FetchedPlugin plugin : fetchFrom(client, source)) {
                    String key = plugin.slug() != null && !plugin.slug().isBlank() ? plugin.slug() : plugin.name();
                    merged.putIfAbsent(key, plugin);
                }
            }
        } catch (IOException | InterruptedException | JSONException e) {
            throw new FetchPluginsServiceException(e);
        }

        return new ArrayList<>(merged.values());
    }

    private List<FetchedPlugin> fetchFrom(HttpClient client, String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) {
            throw new IOException("Could not fetch plugins from " + url + " (HTTP " + response.statusCode() + ").");
        }

        JSONArray jsonArray = new JSONArray(response.body());
        List<FetchedPlugin> plugins = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);

            plugins.add(new FetchedPlugin(
                    obj.optString("name"),
                    obj.optString("slug"),
                    obj.optString("description"),
                    obj.optString("author"),
                    obj.optBoolean("verified", false),
                    obj.optString("downloadUrl"),
                    obj.optString("homepageUrl")
            ));
        }

        return plugins;
    }
}
