package net.omc.managers;

import net.omc.OMCPlugin;
import net.omc.config.LicenseConfig;

import java.io.IOException;

// store System.currentTimeInMillis() in ./plugins/<omc plugin>/lib/license-cache.dat
// should only be checked once every day to prevent rate limits
public class CacheManager {

    private static final long VALID_CACHE = 24 * 60 * 60 * 1000; // 24h

    private final OMCPlugin plugin;
    private final LicenseConfig config;
    private boolean valid = false;

    public CacheManager(OMCPlugin plugin) {
        this.plugin = plugin;
        this.config = new LicenseConfig(plugin, "license-cache.dat");
    }

    public void invalidate() {
        this.valid = false;
    }

    public void loadCache() {
        if (isCacheValid())
            return;

        try {
            config.load(true);
        } catch (IOException e) {
            plugin.error("Something went wrong loading cache.", e); // TODO REMOVE since it is prohibited to show errors
        }
    }

    public boolean isCacheValid() {
        return this.valid && getCurrentCache() != -1 && (System.currentTimeMillis() - getCurrentCache()) < VALID_CACHE;
    }

    public void revalidateCache() {
        config.set("cache", System.currentTimeMillis());
        config.save();
        this.valid = true;
    }

    public long getCurrentCache() {
        return config.contains("cache") ? config.getLong("cache") : -1;
    }

    public LicenseConfig getConfig() {
        return config;
    }
}
