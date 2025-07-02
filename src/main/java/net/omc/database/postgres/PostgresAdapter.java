package net.omc.database.postgres;

import net.omc.OMCPlugin;
import net.omc.database.DatabaseAdapter;
import net.omc.database.OMCDatabase;
import net.omc.handlers.DatabaseHandler;

import java.util.Map;

public abstract class PostgresAdapter implements DatabaseAdapter {

    private final OMCPlugin plugin;
    private final PostgresDatabase database;

    public PostgresAdapter(OMCPlugin plugin, PostgresDatabase database) {
        this.plugin = plugin;
        this.database = database;
    }

    public static PostgresAdapter from(DatabaseAdapter adapter) {
        return adapter instanceof PostgresAdapter ? ((PostgresAdapter) adapter) : null;
    }

    public static PostgresAdapter adapt() {
        return from(DatabaseHandler.ADAPTER);
    }

    public abstract void initDatabase(); // boilerplate

    public abstract void lastSaveMap(); // boilerplate

    public abstract void saveMap(Map<String, Boolean> players); // boilerplate

    public abstract void setToCache(String playerName); // boilerplate

    @Override
    public boolean connect() {
        return this.database.connectConfig();
    }

    @Override
    public boolean isEnabled() {
        return database != null && database.isEnabled();
    }

    @Override
    public boolean existsInDatabase(String playerName) { // inconsistent, use ISQLDatabase#handleExists(String)
        return this.database.fetchExists(playerName);
    }

    @Override
    public void savePlayer(String playerName, Boolean value) {
        try {
            this.database.savePlayer(playerName, value, true);
        } catch (Exception e) {
            plugin.error("Could not save database properly", e);
        }
    }

    @Override
    public void closeDatabase() {
        try {
            if (isEnabled()) {
                database.close();
                plugin.sendConsole(plugin.getDBMessageHandler().getDBDisconnected(toString()));
            }
        } catch (Exception e) {
            plugin.error("Something went wrong closing database connection: ", e);
        }
    }

    @Override
    public boolean getValue(String playerName) {
        return this.database.fetchEnabled(playerName);
    }

    @Override
    public OMCDatabase getDatabase() {
        return this.database;
    }

    @Override
    public OMCDatabase.Type getType() {
        return OMCDatabase.Type.POSTGRESQL;
    }

    @Override
    public String toString() {
        return "PostgreSQL";
    }
}
