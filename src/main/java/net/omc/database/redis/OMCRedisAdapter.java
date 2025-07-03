package net.omc.database.redis;

import net.omc.OMCPlugin;
import net.omc.database.DatabaseAdapter;
import net.omc.database.OMCDatabase;
import net.omc.handlers.OMCDatabaseHandler;

import java.util.Map;

public abstract class OMCRedisAdapter implements DatabaseAdapter {

    public final OMCPlugin plugin;

    public final OMCRedisDatabase redis;

    public OMCRedisAdapter(OMCPlugin plugin, OMCRedisDatabase redis) {
        this.plugin = plugin;
        this.redis = redis;
    }

    public static OMCRedisAdapter from(DatabaseAdapter adapter) {
        return adapter instanceof OMCRedisAdapter ? ((OMCRedisAdapter) adapter) : null;
    }

    public static OMCRedisAdapter adapt() {
        return from(OMCDatabaseHandler.ADAPTER);
    }

    @Override
    public void initDatabase() {
        // NONE
    }

    public abstract void lastSaveMap(); // boilerplate

    public abstract void saveMap(Map<String, Boolean> players); // boilerplate

    public abstract void setToCache(String playerName); // boilerplate

    @Override
    public boolean connect() {
        return redis.connectConfig();
    }

    @Override
    public boolean isEnabled() {
        return redis != null && redis.isEnabled();
    }

    @Override
    public boolean existsInDatabase(String playerName) {
        return redis.hashExists(playerName);
    }

    @Override
    public void savePlayer(String playerName, Boolean value) {
        redis.asyncHashSet(playerName, value.toString());
    }

    @Override
    public void closeDatabase() {
        try {
            if (isEnabled()) {
                redis.close();
                plugin.sendConsole(plugin.getDBMessageHandler().getDBDisconnected());
            }
        } catch (Exception e) {
            plugin.error("Something went wrong closing database connection: ", e);
        }
    }

    @Override
    public boolean getValue(String playerName) {
        return Boolean.parseBoolean(redis.syncGet(playerName));
    }

    @Override
    public OMCDatabase getDatabase() {
        return this.redis;
    }

    @Override
    public OMCDatabase.Type getType() {
        return OMCDatabase.Type.REDIS;
    }

    @Override
    public String toString() {
        return "REDIS";
    }
}