package net.omc.managers;

import net.omc.OMCPlugin;
import net.omc.handlers.HttpHandler;
import org.json.JSONObject;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GitManager {

    private final OMCPlugin plugin;

    public GitManager(OMCPlugin plugin) {
        this.plugin = plugin;
    }

    public String getTagName(String user, String repo) {
        final String API_URL = String.format("https://api.github.com/repos/%s/%s/releases", user, repo);

        try {
            HttpRequest request = HttpHandler.buildRequest(API_URL, HttpHandler.Type.GITHUB)
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();

            if (request == null) {
                plugin.error("Unable to establish connection to API.");
                return "NULL";
            }

            HttpResponse<String> response = HttpHandler.getClient().send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONObject object = new JSONObject(response.body());

                return object.getString("tag_name");
            }

            plugin.error("Something went wrong while checking for updates. HTTP Code: " + response.statusCode());
        } catch (IOException e) {
            plugin.error("Something went wrong while checking for updates.", e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return "NULL";
    }
}
