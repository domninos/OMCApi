package net.omc.database.flatfile;

import net.omc.OMCPlugin;
import net.omc.database.DatabaseAdapter;
import net.omc.database.OMCDatabase;
import net.omc.handlers.OMCDatabaseHandler;

import java.util.Map;

public abstract class OMCFlatFileAdapter implements DatabaseAdapter {
    // extend this to implement

    public final OMCPlugin plugin;
    public final OMCFlatFileDatabase database;

    public OMCFlatFileAdapter(OMCPlugin plugin, OMCFlatFileDatabase database) {
        this.plugin = plugin;
        this.database = database;
    }

    public static OMCFlatFileAdapter from(DatabaseAdapter adapter) {
        return adapter instanceof OMCFlatFileAdapter ? ((OMCFlatFileAdapter) adapter) : null;
    }

    public static OMCFlatFileAdapter adapt() {
        return from(OMCDatabaseHandler.ADAPTER);
    }

    public abstract void initDatabase(); // boilerplate

    public abstract void lastSaveMap(); // boilerplate

    public abstract void saveMap(Map<String, Boolean> players); // boilerplate

    public abstract void setToCache(String playerName); // boilerplate


    @Override
    public boolean connect() {
        return database.connect();
    }

    @Override
    public boolean isEnabled() {
        return database != null && database.isEnabled();
    }

    @Override
    public boolean existsInDatabase(String playerName) {
        return database.has(playerName);
    }

    @Override
    public void savePlayer(String playerName, Boolean value) {
        database.savePlayer(playerName, value); // SAVE TO FILE
    }

    @Override
    public void closeDatabase() {
        try {
            if (isEnabled()) {
                database.close();
                plugin.sendConsole(plugin.getDBMessageHandler().getDBDisconnected());
            }
        } catch (Exception e) {
            plugin.error("Something went wrong closing database: ", e);
        }
    }

    @Override
    public boolean getValue(String playerName) {
        return this.database.getValue(playerName);
    }

    @Override
    public OMCDatabase getDatabase() {
        return this.database;
    }

    @Override
    public OMCDatabase.Type getType() {
        return OMCDatabase.Type.FLAT_FILE;
    }

    @Override
    public String toString() {
        return "FLAT-FILE";
    }
}
