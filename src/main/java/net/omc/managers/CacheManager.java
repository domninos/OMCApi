package net.omc.managers;

import net.omc.OMCPlugin;
import net.omc.util.Cryptography;
import net.omc.util.LicenseConfig;

import java.io.IOException;

// store System.currentTimeInMillis() in ./plugins/<omc plugin>/lib/license-cache.dat
// should only be checked every day to prevent rate limits
public class CacheManager {

    private final long cacheValid = 24 * 60 * 60 * 1000; // 24h

    private final OMCPlugin plugin;
    private final LicenseConfig config;

    public CacheManager(OMCPlugin plugin) {
        this.plugin = plugin;
        this.config = new LicenseConfig(plugin, "license-cache.dat", new Cryptography(plugin));
    }

    public void loadCache() {
        try {
            config.load(true);
        } catch (IOException e) {
            plugin.error("Something went wrong loading cache.", e); // TODO REMOVE since it is prohibited to show errors
        }
    }

    public boolean isCacheValid() {
        return (System.currentTimeMillis() - getCurrentCache()) < cacheValid;
    }

    public void updateCache() {
        config.set("cache", System.currentTimeMillis());
    }

    public long getCurrentCache() {
        return config.contains("cache") ? config.getLong("cache") : -1;
    }
}
