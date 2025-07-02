package net.omc.handlers;

import net.omc.OMCPlugin;
import net.omc.config.ConfigAbstract;
import net.omc.config.OMCConfig;
import net.omc.config.ValueBuilder;
import net.omc.config.ValueType;

public class OMCMessageHandler extends ConfigAbstract {
    public OMCMessageHandler(OMCPlugin plugin) {
        super(plugin);
    }

    @Override
    public void initialize() {
        ValueBuilder builder = load(new OMCConfig(plugin, "messages.yml", true));

        builder.fromConfig()
                .load("db_connected", ValueType.STRING, "[%db_type%] &aSuccessfully connected to database!")
                .load("db_connected_console", ValueType.STRING, "[%db_type%] &aSuccessfully connected to: &3%host%")
                .load("db_switch_warning", ValueType.STRING, "&c&l&oWARNING! &cMay cause data inconsistency. This command is discouraged. Stop the server and reconfigure config.yml. Run this command again to confirm database switch.")
                .load("db_switch_arg", ValueType.STRING, "&aAvailable databases: %databases%.")
                .load("db_disconnected", ValueType.STRING, "[%db_type%] &aDatabase disconnected.")
                .load("db_error_credentials_not_found", ValueType.STRING, "Database information not found in config.yml. Will not use database...")
                .load("db_error_connect_unsuccessful", ValueType.STRING, "[%db_type%] &cNot successful.")
                .load("db_error_connect_disabled", ValueType.STRING, "[%db_type%] &cCould not connect to database because database is disabled.")
                .load("db_error_connect_already", ValueType.STRING, "[%db_type%] &cCould not connect to database because database is already enabled.")
                .load("db_saved", ValueType.STRING, "[%db_type%] &aSaved database.")

                .load("library_loaded", ValueType.STRING, "&aLoaded %library% libraries.")
                .load("library_downloading", ValueType.STRING, "&aDownloading libraries. Please wait for a few seconds.")
                .save();
    }

    @Override
    public void saveToConfig() {
        builder.saveAll();
    }

    public String getLibraryLoaded(String library) {
        return plugin.translate(getString("library_loaded")).replace("%library%", library);
    }

    public String getLibraryDownloading() {
        return plugin.translate(getString("library_downloading"));
    }

    public String getDBConnected(String type) {
        return modifyDBMessage(getString("db_connected"), type);
    }

    public String getDBConnectedConsole(String type, String host) {
        return modifyDBMessage(getString("db_connected_console"), type).replace("%host%", host);
    }

    public String getDBSwitchWarning() {
        return plugin.translate(getString("db_switch_warning"));
    }

    public String getDBSwitchArg(String databases) {
        return plugin.translate(getString("db_switch_arg")).replace("%databases%", databases);
    }

    public String getDBDisconnected(String type) {
        return modifyDBMessage(getString("db_disconnected"), type);
    }

    public String getDBErrorCredentialsNotFound() {
        return plugin.translate(getString("db_error_credentials_not_found"));
    }

    public String getDBErrorConnectUnsuccessful(String type) {
        return modifyDBMessage(getString("db_error_connect_unsuccessful"), type);

    }

    public String getDBErrorConnectDisabled(String type) {
        return modifyDBMessage(getString("db_error_connect_disabled"), type);
    }

    public String getDBErrorConnectedAlready(String type) {
        return modifyDBMessage(getString("db_error_connect_already"), type);
    }

    public String getDatabaseSaved(String type) {
        return modifyDBMessage(getString("db_saved"), type);
    }

    public String modifyDBMessage(String db_message, String type) {
        return plugin.translate(db_message.replace("%db_type%", type.toUpperCase()));
    }
}
