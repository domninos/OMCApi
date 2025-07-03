package net.omc.handlers;

import net.omc.OMCPlugin;
import net.omc.config.ConfigAbstract;
import net.omc.config.value.ValueType;
import net.omc.database.DatabaseAdapter;
import net.omc.database.OMCDatabase;

public class OMCMessageHandler extends ConfigAbstract {
    public OMCMessageHandler(OMCPlugin plugin) {
        super(plugin);
    }

    @Override
    public void initialize() {
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
                .load("db_try", ValueType.STRING, "&aTrying to connect..")
                .load("db_init", ValueType.STRING, "&aInitializing &7%db_type%")
                .load("db_try_save", ValueType.STRING, "[%db_type%] &cTrying to save database.. (processing %updates% updates)")

                .load("library_loaded", ValueType.STRING, "&aLoaded %library% libraries.")
                .load("library_downloading", ValueType.STRING, "&aDownloading libraries. Please wait for a few seconds.")
                .save();
    }

    @Override
    public void saveToConfig() {
        builder.saveAll();
    }

    public String getDBTry() {
        return plugin.translate(getString("db_try"));
    }

    public String getDBInit() {
        return modifyDBMessage(getString("db_init"));
    }

    public String getDBTrySave(int updates) {
        return modifyDBMessage(getString("db_try_save")).replace("%updates%", String.valueOf(updates));
    }

    public String getLibraryLoaded(String library) {
        return plugin.translate(getString("library_loaded")).replace("%library%", library);
    }

    public String getLibraryDownloading() {
        return plugin.translate(getString("library_downloading"));
    }

    public String getDBConnected() {
        return modifyDBMessage(getString("db_connected"));
    }

    public String getDBConnectedConsole(String host) {
        return modifyDBMessage(getString("db_connected_console")).replace("%host%", host);
    }

    public String getDBSwitchWarning() {
        return plugin.translate(getString("db_switch_warning"));
    }

    public String getDBSwitchArg() {
        return plugin.translate(getString("db_switch_arg")).replace("%databases%", OMCDatabase.Type.available());
    }

    public String getDBDisconnected() {
        return modifyDBMessage(getString("db_disconnected"));
    }

    public String getDBErrorCredentialsNotFound() {
        return plugin.translate(getString("db_error_credentials_not_found"));
    }

    public String getDBErrorConnectUnsuccessful() {
        return modifyDBMessage(getString("db_error_connect_unsuccessful"));
    }

    public String getDBErrorConnectDisabled() {
        return modifyDBMessage(getString("db_error_connect_disabled"));
    }

    public String getDBErrorConnectedAlready() {
        return modifyDBMessage(getString("db_error_connect_already"));
    }


    public String getDatabaseSaved() {
        return modifyDBMessage(getString("db_saved"));
    }

    public String modifyDBMessage(String db_message) {
        DatabaseAdapter adapter = OMCDatabaseHandler.ADAPTER;

        return plugin.translate(db_message.replace("%db_type%", adapter == null
                ? plugin.getDBConfigHandler().getDatabaseType().getFormal() : adapter.getType().getFormal()));
    }
}
