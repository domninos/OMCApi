package net.omc.database.flatfile;

import net.omc.OMCPlugin;
import net.omc.database.DatabaseAdapter;
import net.omc.database.OMCDatabase;
import net.omc.handlers.DatabaseHandler;

import java.util.Map;

public abstract class FlatFileAdapter implements DatabaseAdapter {
    // extend this to implement

    private final OMCPlugin plugin;
    private final FlatFileDatabase database;

    public FlatFileAdapter(OMCPlugin plugin, FlatFileDatabase database) {
        this.plugin = plugin;
        this.database = database;
    }

    public static FlatFileAdapter from(DatabaseAdapter adapter) {
        return adapter instanceof FlatFileAdapter ? ((FlatFileAdapter) adapter) : null;
    }

    public static FlatFileAdapter adapt() {
        return from(DatabaseHandler.ADAPTER);
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
                plugin.sendConsole(plugin.getDBMessageHandler().getDBDisconnected(toString()));
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
