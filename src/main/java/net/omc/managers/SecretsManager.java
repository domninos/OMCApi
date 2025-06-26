package net.omc.managers;

import net.omc.OMCPlugin;
import net.omc.util.LicenseConfig;

public class SecretsManager {
    /*
    TODO:
      .
      Store DB credentials on .dat
          - DB_URL (-enrypt-)
          - DB_TOKEN (-encrypt-)
     */

    // use TokenProvider for every token in variables


    // this class contains retrieving encrypted and decrypted texts from external.dat.

    // TODO
    //  store decryption key on ./plugins/<omc plugin>/lib/loader.dat


    private final OMCPlugin plugin;
    private final LicenseConfig config;
    private String key; // TODO fetch from serverless api


    public SecretsManager(OMCPlugin plugin) {
        this.plugin = plugin;
        this.config = new LicenseConfig(plugin, "loader.dat");
    }

    public void loadTokens() {

    }

    public void loadKey() {
        // TODO load from serverless api
    }

    // TODO KEY


    // Connection conn = DriverManager.getConnection("jdbc:postgresql://aws-0-ap-southeast-1.pooler.supabase.com:5432/postgres?user=postgres.cjuvpugyzospthkjonkz&password=[YOUR-PASSWORD]");

}
