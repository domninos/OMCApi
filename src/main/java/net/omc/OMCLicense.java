package net.omc;

import net.omc.managers.LicenseManager;
import net.omc.managers.SecretsManager;

// OMC Plugins API
public class OMCLicense {

    private static final OMCLicense INSTANCE;

    static {
        INSTANCE = new OMCLicense();
    }

    private LicenseManager licenseManager;
    private SecretsManager secretsManager;

    private OMCLicense() {
    }

    public LicenseManager getLicenseManager() {
        return this.licenseManager;
    }

    public SecretsManager getSecretsManager() {
        return this.secretsManager;
    }

    public static OMCLicense getInstance() {
        return INSTANCE;
    }

    public LicenseManager loadLicenseManager(OMCPlugin plugin) {
        if (this.licenseManager == null)
            this.licenseManager = new LicenseManager(plugin);

        return getLicenseManager();
    }

    public SecretsManager loadSecretsManager(OMCPlugin plugin) {
        if (this.secretsManager == null)
            this.secretsManager = new SecretsManager(plugin);

        return getSecretsManager();
    }
}
