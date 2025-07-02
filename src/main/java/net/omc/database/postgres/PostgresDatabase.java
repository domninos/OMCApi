package net.omc.database.postgres;

import net.omc.OMCPlugin;
import net.omc.database.ISQLDatabase;
import net.omc.database.OMCDatabase;
import net.omc.database.SQLCredentials;
import net.omc.util.MainUtil;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


public abstract class PostgresDatabase implements OMCDatabase, ISQLDatabase, SQLCredentials {

    private boolean enabled = false;
    private String host = "N/A";

    private final String tableName;

    private final OMCPlugin plugin;

    public PostgresDatabase(OMCPlugin plugin, String tableName) {
        this.plugin = plugin;
        this.tableName = tableName;
    }

    @Override
    public boolean connect(String host, int port, String database_name, String user, String password) {
        this.host = host;

        if (isEnabled()) {
            plugin.error(plugin.getDBMessageHandler().getDBErrorConnectedAlready(getType()));
            return false;
        }

        try {
            plugin.getHikariManager().initPool(host, port, database_name, user, password);

            Connection connection = plugin.getHikariManager().getConnection();

            if (connection != null) {
                plugin.sendConsole(plugin.getDBMessageHandler().getDBConnectedConsole(host, getType()));

                checkTable();

                connection.close();
                this.enabled = true;

                this.host = host;
            }
        } catch (Exception e) {
            plugin.error(plugin.getDBMessageHandler().getDBErrorConnectUnsuccessful(getType()), e);
            return false;
        }

        return true;
    }

    public abstract void saveNonExists(String playerName, Boolean value);

    @Override
    public String getTableName() {
        return this.tableName;
    }

    @Override
    public String getHost() {
        return this.host;
    }

    @Override
    public boolean connectConfig() {
        String host, user, password, database_name;
        int port;

        if (plugin.getDBConfigHandler().checkDev()) {
            host = "localhost";
            port = 5432;
            database_name = "postgres";
            user = "postgres";
            password = "admin"; // TODO: REMOVE AFTER FINISHING PLUGIN

            plugin.sendConsole("&b[DEV] &aEnabled.");
        } else {
            host = plugin.getDBConfigHandler().getString("host");
            port = plugin.getDBConfigHandler().getInt("port");
            database_name = plugin.getDBConfigHandler().getString("database-name");
            user = plugin.getDBConfigHandler().getString("user");
            password = plugin.getDBConfigHandler().getString("password");

            if (MainUtil.isNullOrBlank(host, user, password)) {
                plugin.error(plugin.getDBMessageHandler().getDBErrorCredentialsNotFound());
                return false;
            }
        }

        return connect(host, port, database_name, user, password);
    }

    @Override
    public CompletableFuture<Boolean> exists(String playerName) {
        if (!isEnabled()) {
            plugin.error(plugin.getDBMessageHandler().getDBErrorConnectDisabled(getType()));
            return null;
        }

        CompletableFuture<Boolean> future = new CompletableFuture<>();

        future.completeAsync(() -> {
            String query = "SELECT 1 FROM " + getTableName() + " WHERE player_name=? LIMIT 1;";

            try (Connection connection = plugin.getHikariManager().getConnection();
                 PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, playerName);

                // EXISTS
                try (ResultSet resultSet = stmt.executeQuery()) {
                    return resultSet.next();
                }
            } catch (SQLException e) {
                plugin.error("Something went wrong checking for existence.", e);
                return false;
            }

        });

        return future;
    }

    @Override
    public void savePlayer(String playerName, Boolean value, boolean async) {
        if (!isEnabled()) {
            plugin.error(plugin.getDBMessageHandler().getDBErrorConnectDisabled(getType()));
            return;
        }

        if (async)
            Bukkit.getScheduler().runTaskAsynchronously(plugin.getJavaPlugin(), () -> saveCallback(playerName, value));
        else
            saveCallback(playerName, value);
    }

    @Override
    public void saveCallbackMap(Map<String, Boolean> enabledPlayers) {
        if (!isEnabled()) {
            plugin.error(plugin.getDBMessageHandler().getDBErrorConnectDisabled(getType()));
            return;
        }

        final String query = "UPDATE " + getTableName() + " SET enabled=? WHERE player_name=?;";

        try (Connection connection = plugin.getHikariManager().getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            connection.setAutoCommit(false);

            for (Map.Entry<String, Boolean> entry : enabledPlayers.entrySet()) {
                String name = entry.getKey();
                Boolean value = entry.getValue();

                stmt.setBoolean(1, value);
                stmt.setString(2, name);
                stmt.addBatch();
            }

            stmt.executeBatch();
            connection.commit();

            plugin.sendConsole(plugin.getDBMessageHandler().getDatabaseSaved(getType()));
        } catch (SQLException e) {
            plugin.error("Something went wrong saving database.", e);
        }
    }

    @Override
    public void saveCallback(String playerName, Boolean value) {
        if (!isEnabled()) {
            plugin.error(plugin.getDBMessageHandler().getDBErrorConnectDisabled(getType()));
            return;
        }

        String query = "UPDATE " + getTableName() + " SET enabled=? WHERE player_name=?;";

        try (Connection connection = plugin.getHikariManager().getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setBoolean(1, value);
            stmt.setString(2, playerName);

            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.error("Something went wrong saving database.", e);
        }
    }

    @Override
    public void handleExists(String playerName) {
        if (!isEnabled()) {
            plugin.error(plugin.getDBMessageHandler().getDBErrorConnectDisabled(getType()));
            return;
        }

        exists(playerName).whenComplete((value, throwable) -> {
            if (throwable != null) {
                plugin.error("Something went wrong handling SQL exists.", throwable);
                return;
            }

            if (!value)
                saveNonExists(playerName, false);
        });
    }

    @Override
    public void insert(String playerName, Boolean value) {
        if (!isEnabled()) {
            plugin.error(plugin.getDBMessageHandler().getDBErrorConnectDisabled(getType()));
            return;
        }

        String query = "INSERT INTO " + getTableName() + "(player_name,enabled) VALUES(?,?);";

        try (Connection connection = plugin.getHikariManager().getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, playerName);
            stmt.setBoolean(2, value);

            stmt.executeUpdate();

        } catch (SQLException e) {
            plugin.error("Something went wrong inserting to database.", e);
        }
    }

    @Override
    public CompletableFuture<Boolean> get(String playerName) {
        if (!isEnabled()) {
            plugin.error(plugin.getDBMessageHandler().getDBErrorConnectDisabled(getType()));
            return null;
        }

        CompletableFuture<Boolean> future = new CompletableFuture<>();

        future.completeAsync(() -> {
            String query = "SELECT enabled FROM " + getTableName() + " WHERE player_name=?;";

            try (Connection connection = plugin.getHikariManager().getConnection();
                 PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, playerName);

                ResultSet resultSet = stmt.executeQuery();

                if (resultSet.next())
                    return resultSet.getBoolean("enabled");

                return false;
            } catch (SQLException e) {
                plugin.error("Something went wrong fetching from database.", e);
                return false;
            }
        });

        return future;
    }

    @Override
    public void checkTable() {
        try (Connection connection = plugin.getHikariManager().getConnection()) {
            String create = "CREATE TABLE IF NOT EXISTS " + getTableName() + "(player_name varchar(36), enabled BOOLEAN)";

            try (PreparedStatement stmt = connection.prepareStatement(create)) {
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            plugin.error("Something went wrong checking database table.", e);
        }
    }

    @Override
    public void saveMap(Map<String, Boolean> enabledPlayers, boolean async) {
        if (!isEnabled()) {
            plugin.error(plugin.getDBMessageHandler().getDBErrorConnectDisabled(getType()));
            return;
        }

        if (async)
            Bukkit.getScheduler().runTaskAsynchronously(plugin.getJavaPlugin(), () -> saveCallbackMap(enabledPlayers));
        else
            saveCallbackMap(enabledPlayers);
    }

    @Override
    public boolean fetchExists(String playerName) {
        if (!isEnabled()) {
            plugin.error(plugin.getDBMessageHandler().getDBErrorConnectDisabled(getType()));
            return false;
        }

        try {
            return Objects.requireNonNull(exists(playerName)).get();
        } catch (InterruptedException | ExecutionException e) {
            plugin.error("Something went wrong fetching exists.", e);
            return false;
        }
    }

    @Override
    public boolean fetchEnabled(String playerName) {
        if (!isEnabled()) {
            plugin.error(plugin.getDBMessageHandler().getDBErrorConnectDisabled(getType()));
            return false;
        }

        try {
            return Objects.requireNonNull(get(playerName)).get();
        } catch (InterruptedException | ExecutionException e) {
            plugin.error("Something went wrong fetching enabled.", e);
            return false;
        }
    }

    @Override
    public void close() {
        try {
            if (!isEnabled())
                return;

            plugin.getHikariManager().close();
            this.enabled = false;
        } catch (Exception e) {
            plugin.error("Something went wrong closing database: ", e);
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String getType() {
        return "PostgreSQL";
    }
}
