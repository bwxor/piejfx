package com.bwxor.piejfx.service;

import com.bwxor.piejfx.dto.FetchedPlugin;
import com.bwxor.piejfx.exception.FetchPluginsServiceException;
import kotlin.jvm.internal.TypeReference;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class FetchPluginsService {

    private static final String API_URL = "https://bwxor.com/api/plugins";

    public List<FetchedPlugin> fetchPlugins() throws FetchPluginsServiceException {
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .GET()
                    .build();

            response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );
        } catch (IOException | InterruptedException e) {
            throw new FetchPluginsServiceException(e);
        }

        JSONArray jsonArray = new JSONArray(response.body());

        List<FetchedPlugin> plugins = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);

            plugins.add(new FetchedPlugin(
                    obj.optString("name"),
                    obj.optString("description"),
                    obj.optString("author"),
                    obj.optString("url")
            ));
        }

        return plugins;
    }
}
