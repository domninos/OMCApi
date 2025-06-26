package net.omc.managers;

import net.omc.OMCPlugin;

/*
  TODO:
     License plugin.
        Add license.
        Add license text on all classes


  TODO: make this into a maven repository hosted on github for free
 */
public class LicenseManager {

    private final OMCPlugin plugin;
    private final CacheManager cacheManager;

    public LicenseManager(OMCPlugin plugin) {
        this.plugin = plugin;
        this.cacheManager = new CacheManager(plugin);
    }

    public void loadLicense() {
        // TODO
    }

    public boolean isLicenseValid() {
        return false; // TODO
    }

    // server_id = network_id
    public void getLicense(String server_id) { // retrieve data from supabase

    }
}
