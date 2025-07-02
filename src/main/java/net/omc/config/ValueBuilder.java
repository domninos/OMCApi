package net.omc.config;

import net.omc.util.MainUtil;

import java.util.Map;

public class ValueBuilder {

    private final OMCConfig config;
    private final Map<String, String> STRING_VALUES;
    private final Map<String, Integer> INT_VALUES;
    private final Map<String, Boolean> BOOL_VALUES;

    public ValueBuilder(OMCConfig config,
                        Map<String, String> STRING_VALUES,
                        Map<String, Integer> INT_VALUES,
                        Map<String, Boolean> BOOL_VALUES) {
        this.config = config;

        this.STRING_VALUES = STRING_VALUES;
        this.INT_VALUES = INT_VALUES;
        this.BOOL_VALUES = BOOL_VALUES;
    }

    private boolean fromConfigYaml = false;
    private boolean fromPluginYaml = false;
    private boolean hasDef = false;
    private boolean hasSave = false;

    private void checkDefault(String path, ValueType type, ValueDef defaultValue) {
        if (type == ValueType.STRING) {
            if (config.getString(path) == null) {
                config.setNoSave(path, defaultValue.asString());
                hasDef = true;
            }
        } else if (type == ValueType.INT) {
            if (config.getInt(path) == 0) {
                config.setNoSave(path, defaultValue.asInt());
                hasDef = true;
            }
        } else if (type == ValueType.BOOLEAN) {
            if (config.getString(path) == null) {
                config.setNoSave(path, defaultValue.asBool());
                hasDef = true;
            }
        }
    }

    public ValueBuilder load(String path, ValueType type, ValueDef defaultValue) {
        if (fromConfigYaml) {

            checkDefault(path, type, defaultValue);

            if (type == ValueType.STRING)
                STRING_VALUES.put(path, config.getString(path));
            else if (type == ValueType.INT)
                INT_VALUES.put(path, config.getInt(path));
            else if (type == ValueType.BOOLEAN)
                BOOL_VALUES.put(path, config.getBool(path));


        } else if (fromPluginYaml) {
            if (type == ValueType.PLUGIN_NAME)
                STRING_VALUES.put(path, config.getPlugin().getDescription().getName());
            else if (type == ValueType.PLUGIN_VERSION)
                STRING_VALUES.put(path, config.getPlugin().getDescription().getVersion());
            else if (type == ValueType.PLUGIN_API)
                STRING_VALUES.put(path, MainUtil.VERSION >= 13 ? config.getPlugin().getDescription().getAPIVersion() : MainUtil.FULL_VERSION);
        }
        return this;
    }

    public ValueBuilder load(String path, ValueType type, String def) {
        return load(path, type, ValueDef.from(def));
    }

    public ValueBuilder load(String path, ValueType type, int def) {
        return load(path, type, ValueDef.from(def));
    }

    public ValueBuilder load(String path, ValueType type, boolean def) {
        return load(path, type, ValueDef.from(def));
    }

    public ValueBuilder fromConfig() {
        this.fromPluginYaml = false;
        this.fromConfigYaml = true;
        return this;
    }

    public ValueBuilder fromPlugin() {
        this.fromConfigYaml = false;
        this.fromPluginYaml = true;
        return this;
    }

    public ValueBuilder toSave(String path, ValueType type) {
        this.hasSave = true;

        if (type == ValueType.STRING)
            config.setNoSave(path, STRING_VALUES.getOrDefault(path, "N/A"));
        else if (type == ValueType.INT)
            config.setNoSave(path, INT_VALUES.getOrDefault(path, -1));
        else if (type == ValueType.BOOLEAN)
            config.setNoSave(path, BOOL_VALUES.getOrDefault(path, false));

        return this;
    }

    public ValueBuilder save() {
        if (hasSave || hasDef)
            config.save();

        hasSave = false;
        hasDef = false;

        return this;
    }
}
