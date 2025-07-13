package net.omc;

import net.omc.handlers.LibraryHandler;
import net.omc.handlers.OMCConfigHandler;
import net.omc.handlers.OMCMessageHandler;
import net.omc.managers.GitManager;
import net.omc.managers.HikariManager;
import net.omc.managers.LicenseManager;
import net.omc.managers.VersionManager;

// OMC Plugins API
public class OMCApi {

    private static final OMCApi INSTANCE;

    static {
        INSTANCE = new OMCApi();
    }

    private LicenseManager licenseManager;
    private HikariManager hikariManager;
    private OMCMessageHandler OMCMessageHandler;
    private OMCConfigHandler OMCConfigHandler;
    private LibraryHandler libraryHandler;

    private GitManager gitManager;
    private VersionManager versionManager;

    private OMCApi() {
    }


    public static OMCApi getInstance() {
        return INSTANCE;
    }

    public LicenseManager loadLicense(OMCPlugin plugin) {
        if (this.licenseManager == null)
            this.licenseManager = new LicenseManager(plugin);

        return this.licenseManager;
    }

    public VersionManager getVersionManager(OMCPlugin plugin) {
        if (this.versionManager == null)
            this.versionManager = new VersionManager(plugin);

        return this.versionManager;
    }

    public GitManager getGitManager(OMCPlugin plugin) {
        if (this.gitManager == null)
            this.gitManager = new GitManager(plugin);

        return this.gitManager;
    }

    public OMCMessageHandler getDBMessageHandler(OMCPlugin plugin) {
        if (this.OMCMessageHandler == null)
            this.OMCMessageHandler = new OMCMessageHandler(plugin);

        return this.OMCMessageHandler;
    }

    public OMCConfigHandler getDBConfigHandler(OMCPlugin plugin) {
        if (this.OMCConfigHandler == null)
            this.OMCConfigHandler = new OMCConfigHandler(plugin);

        return this.OMCConfigHandler;
    }

    public HikariManager getHikariManager(OMCPlugin plugin) {
        if (this.hikariManager == null)
            this.hikariManager = new HikariManager(plugin);

        return this.hikariManager;
    }

    public LibraryHandler getLibraryHandler(OMCPlugin plugin) {
        if (this.libraryHandler == null)
            this.libraryHandler = new LibraryHandler(plugin);

        return this.libraryHandler;
    }

}
