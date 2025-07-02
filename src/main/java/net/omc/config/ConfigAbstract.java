package net.omc.config;

import net.omc.OMCPlugin;
import net.omc.util.Flushable;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ConfigAbstract implements Flushable {
    private static final Map<String, String> STRING_VALUES = new ConcurrentHashMap<>(); // concurrent since it may be used on a different thread
    private static final Map<String, Integer> INT_VALUES = new ConcurrentHashMap<>(); // concurrent since it may be used on a different thread
    private static final Map<String, Boolean> BOOL_VALUES = new ConcurrentHashMap<>(); // concurrent since it may be used on a different thread

    private final OMCPlugin plugin;

    private OMCConfig omcConfig;

    private ValueBuilder builder;

    public ConfigAbstract(OMCPlugin plugin) {
        this.plugin = plugin;
    }

    public ValueBuilder load() {
        if (this.omcConfig == null)
            this.omcConfig = plugin.getOMCConfig();

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

    public abstract void saveToConfig();

    public FileConfiguration getConfig() {
        return omcConfig.getConfig();
    }

    private ValueBuilder getBuilder() {
        if (this.omcConfig == null)
            this.omcConfig = plugin.getOMCConfig();

        if (this.builder == null)
            this.builder = new ValueBuilder(this.omcConfig, STRING_VALUES, INT_VALUES, BOOL_VALUES);

        return this.builder;
    }

    @Override
    public void flush() {
        STRING_VALUES.clear();
        INT_VALUES.clear();
        BOOL_VALUES.clear();
    }
}
