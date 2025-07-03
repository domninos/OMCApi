package net.omc.config.value;

import net.omc.config.OMCConfig;
import net.omc.util.MainUtil;

import java.util.List;
import java.util.Map;

public class ValueBuilder {

    private final OMCConfig config;
    private final Map<String, String> STRING_VALUES;
    private final Map<String, Integer> INT_VALUES;
    private final Map<String, Boolean> BOOL_VALUES;
    private final Map<String, List<String>> STRING_LIST_VALUES;

    public ValueBuilder(OMCConfig config,
                        Map<String, String> STRING_VALUES,
                        Map<String, Integer> INT_VALUES,
                        Map<String, Boolean> BOOL_VALUES,
                        Map<String, List<String>> STRING_LIST_VALUES) {
        this.config = config;

        this.STRING_VALUES = STRING_VALUES;
        this.INT_VALUES = INT_VALUES;
        this.BOOL_VALUES = BOOL_VALUES;
        this.STRING_LIST_VALUES = STRING_LIST_VALUES;
    }

    private boolean fromConfigYaml = true;
    private boolean fromPluginYaml = false;
    private boolean hasDef = false;
    private boolean hasSave = false;

    private void checkDefault(String path, ValueType type, ValueDef defaultValue) {
        if (type == ValueType.NULL || defaultValue == null || defaultValue.getValue() == null)
            return;

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
        } else if (type == ValueType.STRING_LIST) {
            if (config.getConfig().getStringList(path).isEmpty()) {
                config.setNoSave(path, defaultValue.asStringList());
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
            else if (type == ValueType.BOOLEAN || type == ValueType.NULL)
                BOOL_VALUES.put(path, config.getBool(path));
            else if (type == ValueType.STRING_LIST)
                STRING_LIST_VALUES.put(path, config.getConfig().getStringList(path));

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

    public ValueBuilder load(String path, ValueType type, List<String> def) {
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
        if (type == ValueType.STRING)
            return toSave(path, type, ValueDef.from(STRING_VALUES.getOrDefault(path, "N/A")));
        else if (type == ValueType.INT)
            return toSave(path, type, ValueDef.from(INT_VALUES.getOrDefault(path, -1)));
        else if (type == ValueType.BOOLEAN)
            return toSave(path, type, ValueDef.from(BOOL_VALUES.getOrDefault(path, false)));
        else if (type == ValueType.STRING_LIST)
            return toSave(path, type, ValueDef.from(STRING_LIST_VALUES.getOrDefault(path, ValueDef.EMPTY_LIST)));

        return this;
    }

    public ValueBuilder toSave(String path, ValueType type, ValueDef value) {
        this.hasSave = true;

        if (type == ValueType.STRING)
            config.setNoSave(path, value.asString());
        else if (type == ValueType.INT)
            config.setNoSave(path, value.asInt());
        else if (type == ValueType.BOOLEAN)
            config.setNoSave(path, value.asBool());
        else if (type == ValueType.STRING_LIST)
            config.setNoSave(path, value.asStringList());

        set(path, type, value);

        return this;
    }

    public ValueBuilder set(String path, ValueType type, ValueDef value) {
        if (type == ValueType.STRING)
            STRING_VALUES.put(path, value.asString());
        else if (type == ValueType.INT)
            INT_VALUES.put(path, value.asInt());
        else if (type == ValueType.BOOLEAN)
            BOOL_VALUES.put(path, value.asBool());
        else if (type == ValueType.STRING_LIST)
            STRING_LIST_VALUES.put(path, value.asStringList());

        return this;
    }

    public void saveAll() {
        STRING_VALUES.forEach(config::setNoSave);
        INT_VALUES.forEach(config::setNoSave);
        BOOL_VALUES.forEach(config::setNoSave);
        STRING_LIST_VALUES.forEach(config::setNoSave);
        config.save();
    }

    public ValueBuilder save() {
        if (hasSave || hasDef)
            config.save();

        hasSave = false;
        hasDef = false;

        return this;
    }
}
