package net.omc.handlers;

import net.omc.OMCPlugin;
import net.omc.config.*;
import net.omc.database.OMCDatabase;

public class OMCConfigHandler extends ConfigAbstract {
    public OMCConfigHandler(OMCPlugin plugin) {
        super(plugin);
    }

    @Override
    public void initialize() {
        ValueBuilder builder = load(new OMCConfig(plugin, "config.yml", true));

        builder.fromConfig()
                .load("host", ValueType.STRING, "<put database host here>")
                .load("port", ValueType.INT, 0)
                .load("database-name", ValueType.STRING, "<put database name here>")
                .load("user", ValueType.STRING, "<put database user here>")
                .load("password", ValueType.STRING, "<put database password here>")
                .load("database-type", ValueType.STRING, "sqlite")

                .load("dev", ValueType.BOOLEAN, ValueDef.none())
                .save();
    }
    public OMCDatabase.Type getDatabaseType() {
        return OMCDatabase.Type.valueOf(getString("database-type").toUpperCase().replace("-", "_"));
    }

    public boolean checkDev() {
        return getBool("dev");
    }

    @Override
    public void saveToConfig() {
        builder.fromConfig()
                .toSave("host", ValueType.STRING)
                .toSave("port", ValueType.INT)
                .toSave("user", ValueType.STRING)
                .toSave("password", ValueType.STRING)
                .toSave("database-type", ValueType.STRING)

                .save();
    }
}
