package net.omc.config;

import net.omc.OMCPlugin;
import net.omc.config.value.ValueBuilder;
import net.omc.config.value.ValueDef;
import net.omc.util.Flushable;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ConfigAbstract implements Flushable {
    private final Map<String, String> STRING_VALUES = new ConcurrentHashMap<>(); // concurrent since it may be used on a different thread
    private final Map<String, Integer> INT_VALUES = new ConcurrentHashMap<>(); // concurrent since it may be used on a different thread
    private final Map<String, Boolean> BOOL_VALUES = new ConcurrentHashMap<>(); // concurrent since it may be used on a different thread
    private final Map<String, List<String>> STRING_LIST_VALUES = new ConcurrentHashMap<>();

    public final OMCPlugin plugin;

    private OMCConfig omcConfig;

    public ValueBuilder builder;

    public ConfigAbstract(OMCPlugin plugin) {
        this.plugin = plugin;
    }

    public abstract void initialize();

    public abstract void saveToConfig();

    public void loadValues(ConfigAbstract from) {
        setStringValues(from.getStringValues());
        setIntValues(from.getIntValues());
        setBoolValues(from.getBoolValues());
        setStringListValues(from.getStringListValues());
    }

    public Map<String, String> getStringValues() {
        return STRING_VALUES;
    }

    public void setStringValues(Map<String, String> values) {
        STRING_VALUES.putAll(values);
    }

    public Map<String, Integer> getIntValues() {
        return INT_VALUES;
    }

    public void setIntValues(Map<String, Integer> values) {
        INT_VALUES.putAll(values);
    }

    public Map<String, Boolean> getBoolValues() {
        return BOOL_VALUES;
    }

    public void setBoolValues(Map<String, Boolean> values) {
        BOOL_VALUES.putAll(values);
    }

    public Map<String, List<String>> getStringListValues() {
        return STRING_LIST_VALUES;
    }

    public void setStringListValues(Map<String, List<String>> values) {
        STRING_LIST_VALUES.putAll(values);
    }

    public void reload() {
        omcConfig.reload();
    }

    public ValueBuilder load(OMCConfig config) {
        if (this.omcConfig == null)
            this.omcConfig = config;

        return getBuilder();
    }

    public String getString(String path) {
        return STRING_VALUES.getOrDefault(path, "N/A");
    }

    public int getInt(String path) {
        return INT_VALUES.getOrDefault(path, -1);
    }

    public boolean getBool(String path) {
        return BOOL_VALUES.getOrDefault(path, false);
    }

    public List<String> getStringList(String path) {
        return STRING_LIST_VALUES.getOrDefault(path, ValueDef.EMPTY_LIST);
    }


    public FileConfiguration getConfig() {
        return omcConfig.getConfig();
    }

    public ValueBuilder getBuilder() {
        if (this.omcConfig == null)
            this.omcConfig = plugin.getOMCConfig();

        if (this.builder == null)
            this.builder = new ValueBuilder(this.omcConfig, getStringValues(), getIntValues(), getBoolValues(), getStringListValues());

        return this.builder;
    }

    @Override
    public void flush() {
        STRING_VALUES.clear();
        INT_VALUES.clear();
        BOOL_VALUES.clear();

        if (!STRING_LIST_VALUES.isEmpty())
            STRING_LIST_VALUES.forEach((key, list) -> list.clear());

        STRING_LIST_VALUES.clear();
    }
}
