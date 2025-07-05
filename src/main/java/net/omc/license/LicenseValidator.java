package net.omc.license;

import net.omc.handlers.HttpHandler;
import org.json.JSONObject;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class LicenseValidator {
//
    public static void main(String[] args) {
        System.out.println(checkLicense("nearchat", "18221d7d1126-9IRX-8TB6-YD3F-AJF4"));
    }


    // bought_by should be updated separately. use it through discord bot. /license update <key> <key> <value>

    // should just change network_id, status, ip. license should be already available when doing /license create <plugin>
    public static Status activateLicense(String plugin, String network_id, String license, String status, String ip) {
        String url = "https://mxnuzxiklpdxrgapuusx.supabase.co/rest/v1/rpc/register";

        String jsonPayload = String.format(
                "{\"plugin\":\"%s\",\"network_id\":\"%s\",\"license\":\"%s\",\"status\":\"%s\",\"ip\":\"%s\"}",
                plugin, network_id, license, status, ip
        );

        try {
            HttpRequest request = HttpHandler.buildRequest(url, HttpHandler.Type.SB)
                    .method("POST", HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = HttpHandler.getClient().send(request, HttpResponse.BodyHandlers.ofString());

            JSONObject object = new JSONObject(response.body());
            String responseStatus = object.getString("status");
            String responseMessage = object.getString("message");

            if (responseMessage.contains("duplicate key"))
                return Status.DUPLICATE;

            return Status.get(responseStatus);
        } catch (Exception ignore) {
        }


        return Status.NULL;
    }

    public static Status checkLicense(String plugin, String license) {
        String url = "https://mxnuzxiklpdxrgapuusx.supabase.co/rest/v1/rpc/get_status";

        // Use POST with JSON body for RPC calls
        String jsonPayload = String.format(
                "{\"plugin\":\"%s\",\"license_arg\":\"%s\"}",
                plugin, license
        );

        try {
            HttpRequest request = HttpHandler.buildRequest(url, HttpHandler.Type.SB)
                    .method("POST", HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = HttpHandler.getClient().send(request, HttpResponse.BodyHandlers.ofString());

            JSONObject object = new JSONObject(response.body());

            System.out.println(response.body());

            return Status.get(object.getString("get_license_status"));
        } catch (Exception ignore) {
        }

        return Status.NULL;
    }

}