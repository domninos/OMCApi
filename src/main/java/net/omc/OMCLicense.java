package net.omc;

import net.omc.managers.LicenseManager;

// OMC Plugins API
public class OMCLicense {

    private static final OMCLicense INSTANCE;

    static {
        INSTANCE = new OMCLicense();
    }

    private LicenseManager licenseManager;


    private OMCLicense() {
    }

    public LicenseManager getLicenseManager() {
        return this.licenseManager;
    }


    public static OMCLicense getInstance() {
        return INSTANCE;
    }

    public LicenseManager loadLicenseManager(OMCPlugin plugin) {
        if (this.licenseManager == null)
            this.licenseManager = new LicenseManager(plugin);

        return getLicenseManager();
    }

}
