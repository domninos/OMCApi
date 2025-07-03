package net.omc.handlers;

import net.omc.OMCPlugin;
import net.omc.database.DatabaseAdapter;
import net.omc.database.ISQLDatabase;
import net.omc.database.OMCDatabase;
import net.omc.database.flatfile.OMCFlatFileAdapter;
import net.omc.database.postgres.OMCPostgresAdapter;
import net.omc.database.redis.OMCRedisAdapter;
import net.omc.database.sqlite.OMCSQLiteAdapter;
import org.bukkit.entity.Player;

import java.util.Map;

public abstract class OMCDatabaseHandler {
    public final OMCPlugin plugin;

    private int updates = 0;

    public static DatabaseAdapter ADAPTER;

    public OMCDatabaseHandler(OMCPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isFlatFile() {
        return ADAPTER != null
                && plugin.getDBConfigHandler().getDatabaseType() == OMCDatabase.Type.FLAT_FILE
                && ADAPTER instanceof OMCFlatFileAdapter;
    }

    public boolean isRedis() {
        return ADAPTER != null
                && plugin.getDBConfigHandler().getDatabaseType() == OMCDatabase.Type.REDIS
                && ADAPTER instanceof OMCRedisAdapter;
    }

    public boolean isPostgreSQL() {
        return ADAPTER != null
                && plugin.getDBConfigHandler().getDatabaseType() == OMCDatabase.Type.POSTGRESQL
                && ADAPTER instanceof OMCPostgresAdapter;
    }

    public boolean isSQLite() {
        return ADAPTER != null
                && plugin.getDBConfigHandler().getDatabaseType() == OMCDatabase.Type.SQLITE
                && ADAPTER instanceof OMCSQLiteAdapter;
    }

    public abstract OMCDatabase.Type initDatabase();

    public abstract boolean connect();

    public abstract void setToCache(Player player);

    public void updateChecks() {
        this.updates++;
    }

    public void resetCheckUpdates() {
        this.updates = 0;
    }

    public int getUpdates() {
        return updates;
    }

    public abstract void saveMap(Map<String, Boolean> enabledPlayers, boolean async);

    public void savePlayer(String playerName, Boolean value, boolean async) {
        if (!isEnabled()) {
            plugin.sendConsole(plugin.getDBMessageHandler().getDBErrorConnectDisabled());
            return;
        }
        if (ADAPTER == null) {
            plugin.sendConsole(plugin.getDBMessageHandler().getDBErrorConnectUnsuccessful());
            return;
        }

        // sql async
        if (isSQL()) {
            ISQLDatabase sqlDb = (ISQLDatabase) getAdapter().getDatabase();
            sqlDb.savePlayer(playerName, value, async);
        } else
            ADAPTER.savePlayer(playerName, value);

        updateChecks();
    }

    public void savePlayer(String playerName, String value) {
        savePlayer(playerName, Boolean.getBoolean(value), true);
    }

    public boolean isEnabled() { // TODO make it not check for isLibLoaded for PlayerManager#loadEnabled (?)
        return ADAPTER != null && plugin.getLibraryHandler().isLibLoaded(ADAPTER.getType()) && ADAPTER.isEnabled();
    }

    public boolean checkExistsDB(String playerName) {
        return isEnabled() && ADAPTER.existsInDatabase(playerName);
    }

    public DatabaseAdapter getAdapter() {
        return ADAPTER;
    }

    public boolean isSQL() {
        return ADAPTER != null && ADAPTER.getDatabase() instanceof ISQLDatabase;
    }

    public void closeDatabase() {
        if (isEnabled())
            ADAPTER.closeDatabase();
    }
}
