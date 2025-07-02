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


  TODO: make this into a maven repository hosted on github for free
 */
public class LicenseManager {

    private final OMCPlugin plugin;

    private final CacheManager cacheManager;

    private final NetworkIdGenerator networkIdGenerator;

    private License license;
    private String networkId;

    public LicenseManager(OMCPlugin plugin) {
        this.plugin = plugin;
        this.cacheManager = new CacheManager(plugin);

        this.networkIdGenerator = new NetworkIdGenerator(plugin.getNetworkPrefix());
    }

    public void loadLicenseFromConfig(OMCConfig config) {
        if (config == null)
            return;

        String license = config.getString("license");

        if (license == null) {
        }

//        this.license = new License(Status.NULL);
    }

    // this should be set in config.yml
    private String generateNetworkId() {
        return networkIdGenerator.nextId();
    }

    public String getNetworkId(OMCConfig config) {
        if (this.networkId == null) {
            if (config.getString("network_id") != null) {
                this.networkId = config.getString("network_id");
            } else {
                this.networkId = generateNetworkId();

                config.set("network_id", this.networkId); // auto save
            }
        }

        return this.networkId;
    }

    public String getIp() {
        String serverIP = Bukkit.getIp();

        try {
            return serverIP.isEmpty() ? InetAddress.getLocalHost().getHostAddress() : serverIP;
        } catch (UnknownHostException ignore) {
            // TODO for now error
            ignore.printStackTrace();
            return "N/A";
        }
    }

    public void loadLicense(String network_id, String ip) {
        cacheManager.loadCache();

        if (!cacheManager.isCacheValid()) {
            Status status = LicenseValidator.checkLicense(plugin.getDescription().getName(), network_id, ip);

            if (status != Status.NULL) {
                this.license = new License(status);

                cacheManager.revalidateCache();
            } else {
                // stop caching
                cacheManager.invalidate();
            }
        }
    }

    public boolean setLicense(OMCPlugin plugin, String license) {
        // TODO run this on /nc license <plugin>

        if (license == null || license.isEmpty())
            return false;

        return false;
    }

    public boolean isLicenseValid() {
        return license != null && license.isValid();
    }
}
