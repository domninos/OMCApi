package net.omc.handlers;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;

public class HttpHandler {

    // no need to close. HttpClient closes automatically.
    private static final HttpClient client = HttpClient.newHttpClient();

    public static HttpClient getClient() {
        return client;
    }

    public static HttpRequest.Builder buildRequest(String url, Type type) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Content-Profile", "public")
                .header("Accept-Profile", "public")
                .timeout(Duration.ofSeconds(30));

        if (!type.getToken().isBlank())
            builder
                    .header("Authorization", "Bearer " + type.getToken())
                    .header("apikey", type.getToken());

        return builder;
    }

    public enum Type {
        SB("sb_publishable_XPx-LFkL3eYQwPdXjiO22Q_YTm9k_cG"),
        GITHUB("");

        final String token;

        Type(String token) {
            this.token = token;
        }

        public String getToken() {
            return token;
        }
    }
}
