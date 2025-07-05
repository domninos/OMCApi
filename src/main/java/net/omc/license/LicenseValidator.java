package net.omc.license;

import net.omc.handlers.HttpHandler;
import org.json.JSONObject;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class LicenseValidator {
//
//    public static void main(String[] args) {
//        System.out.println(checkLicense("nearchat", "0123", "192.1691.1.1"));
//    }


    // bought_by should be updated separately. use it through discord bot. /license update <key> <key> <value>

    public static Status activateLicense(String plugin, String network_id, String license, String status, String ip) {
        String url = "https://mxnuzxiklpdxrgapuusx.supabase.co/rest/v1/rpc/activate_license_wrapper";

        String jsonPayload = String.format(
                "{\"plugin\":\"%s\",\"network_id_arg\":\"%s\",\"license\":\"%s\",\"status_arg\":\"%s\",\"ip_arg\":\"%s\"}",
                plugin, network_id, license, status, ip
        );

        try {
            HttpRequest request = HttpHandler.buildRequest(url, HttpHandler.Type.SB)
                    .method("POST", HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = HttpHandler.getClient().send(request, HttpResponse.BodyHandlers.ofString());

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
            HttpRequest request = HttpHandler.buildRequest(url, HttpHandler.Type.SB)
                    .method("POST", HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = HttpHandler.getClient().send(request, HttpResponse.BodyHandlers.ofString());

            JSONObject object = new JSONObject(response.body());

            return Status.get(object.getString("get_license_status"));
        } catch (Exception ignore) {
        }

        return Status.NULL;
    }

}