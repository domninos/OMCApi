package net.omc.handlers;

import net.omc.OMCPlugin;
import net.omc.config.ConfigAbstract;
import net.omc.config.value.ValueDef;
import net.omc.config.value.ValueType;
import net.omc.database.OMCDatabase;

public class OMCConfigHandler extends ConfigAbstract {
    public OMCConfigHandler(OMCPlugin plugin) {
        super(plugin);
    }

    @Override
    public void initialize() {
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

    public void setDatabase(OMCDatabase.Type type) {
        builder.fromConfig().toSave("database-type", ValueType.STRING, ValueDef.from(type.getLabel())).save();
    }

    public boolean checkDev() {
        if (getBool("dev")) {
            if (getConfig().getString("dev") == null)
                builder.set("dev", ValueType.BOOLEAN, ValueDef.from(false));
        }

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
