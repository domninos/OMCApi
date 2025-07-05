package net.omc.managers;

import net.omc.OMCPlugin;
import net.omc.config.OMCConfig;
import net.omc.license.License;
import net.omc.license.LicenseValidator;
import net.omc.license.NetworkIdGenerator;
import net.omc.license.Status;
import org.bukkit.Bukkit;

import java.net.InetAddress;
import java.net.UnknownHostException;

/*
  TODO:
     License plugin.
        Add license text on all classes
 */
public class LicenseManager {

    public final OMCPlugin plugin;
    private final OMCConfig config;

    private final CacheManager cacheManager;

    private final NetworkIdGenerator networkIdGenerator;

    private License license;
    private String networkId;

    // make this work first before
    //        * database to implement: mysql, mariadb, mongodb

    public LicenseManager(OMCPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getOMCConfig();

        this.cacheManager = new CacheManager(plugin);

        this.networkIdGenerator = new NetworkIdGenerator(plugin.getNetworkPrefix());
    }

    public void setup() {
        if (config.getString("network_id") == null) {
            this.networkId = generateNetworkId();

            config.set("network_id", networkId);
        }

        String loaded = loadLicenseFromConfig();
        Status status = loadLicense(loaded);

        if (status == Status.REVOKED)
            plugin.sendConsole("&cYou are using an invalid license.");
    }

    private String loadLicenseFromConfig() {
        if (config == null)
            return "NULL";

        if (config.getString("license") == null)
            config.set("license", "unset"); // default

        return config.getString("license");
    }

    private String generateNetworkId() {
        if (this.networkId == null)
            this.networkId = networkIdGenerator.nextId();

        return this.networkId;
    }

    public String getNetworkId() {
        return config.getString("network_id");
    }

    private String getIp() {
        String serverIP = Bukkit.getIp();

        try {
            return serverIP.isEmpty() ? InetAddress.getLocalHost().getHostAddress() : serverIP;
        } catch (UnknownHostException ignore) {
            // TODO for now error
            ignore.printStackTrace();
            return "N/A";
        }
    }

    private Status loadLicense(String key) {
        cacheManager.loadCache();

        if (!cacheManager.isCacheValid()) {
            Status status = LicenseValidator.checkLicense(plugin.getDescription().getName().toLowerCase(), key);

            if (status != Status.NULL && status != Status.REVOKED) {
                this.license = new License(status);
                license.setKey(loadLicenseFromConfig());

                cacheManager.revalidateCache();
            } else {
                // stop caching
                cacheManager.invalidate();
            }

            return status;
        }

        return Status.NULL;
    }

    public boolean activateLicense(String key) {
        if (key == null || key.isEmpty())
            return false;

        String loaded = loadLicenseFromConfig();

        if (loaded != null && loaded.equals("unset")) {
            plugin.sendConsole("&cLicense already set.");
            return false;
        }

        // check if license is already activated

        if (license == null)
            this.license = new License(Status.ACTIVE);

        license.setKey(key);

        String network_id = getNetworkId();

        Status status = LicenseValidator.activateLicense(
                plugin.getDescription().getName().toLowerCase(), network_id, key, Status.ACTIVE.name(), getIp());

        if (status == Status.DUPLICATE) {
            plugin.sendConsole("&aLicense already activated.");
            return false;
        }

        config.setNoSave("network_id", network_id);
        config.setNoSave("license", key);
        config.save();

        return status != Status.NULL;
    }

    public boolean isLicenseValid() {
        return license != null && license.isValid();
    }
}
