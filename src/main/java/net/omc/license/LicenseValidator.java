package net.omc.license;

import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class LicenseValidator {

    // no need to close. HttpClient closes automatically.
    private static final HttpClient client = HttpClient.newHttpClient();


    // bought_by should be updated separately. use it through discord bot. /license update <key> <key> <value>

    public static Status activateLicense(String plugin, String network_id, String license, String status, String ip) {

        String url = "https://mxnuzxiklpdxrgapuusx.supabase.co/rest/v1/rpc/activate_license_wrapper";

        String jsonPayload = String.format(
                "{\"plugin\":\"%s\",\"network_id_arg\":\"%s\",\"license\":\"%s\",\"status_arg\":\"%s\",\"ip_arg\":\"%s\"}",
                plugin, network_id, license, status, ip
        );

        try {
            HttpRequest request = buildRequest(url)
                    .method("POST", HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JSONObject object = new JSONObject(response.body());

            return Status.get(object.getString("activate_license"));
        } catch (Exception ignore) {
        }


        return Status.NULL;
    }

    public static Status checkLicense(String plugin, String networkId, String ip) {
        String url = "https://mxnuzxiklpdxrgapuusx.supabase.co/rest/v1/rpc/validate_license";

        // Use POST with JSON body for RPC calls
        String jsonPayload = String.format(
                "{\"plugin\":\"%s\",\"network_id_arg\":\"%s\",\"ip_arg\":\"%s\"}",
                plugin, networkId, ip
        );

        try {
            HttpRequest request = buildRequest(url)
                    .method("POST", HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JSONObject object = new JSONObject(response.body());

            return Status.get(object.getString("get_license_status"));
        } catch (Exception ignore) {
        }

        return Status.NULL;
    }

    private static HttpRequest.Builder buildRequest(String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Bearer sb_publishable_XPx-LFkL3eYQwPdXjiO22Q_YTm9k_cG")
                .header("apikey", "sb_publishable_XPx-LFkL3eYQwPdXjiO22Q_YTm9k_cG")
                .header("Content-Profile", "public")
                .header("Accept-Profile", "public")
                .timeout(Duration.ofSeconds(30));
    }

}