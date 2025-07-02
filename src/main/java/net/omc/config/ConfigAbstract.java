package net.omc.config;

import net.omc.OMCPlugin;
import net.omc.util.Flushable;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ConfigAbstract implements Flushable {
    private static final Map<String, String> STRING_VALUES = new ConcurrentHashMap<>(); // concurrent since it may be used on a different thread
    private static final Map<String, Integer> INT_VALUES = new ConcurrentHashMap<>(); // concurrent since it may be used on a different thread
    private static final Map<String, Boolean> BOOL_VALUES = new ConcurrentHashMap<>(); // concurrent since it may be used on a different thread
    private static final Map<String, List<String>> STRING_LIST_VALUES = new ConcurrentHashMap<>();

    public final OMCPlugin plugin;

    private OMCConfig omcConfig;

    public ValueBuilder builder;

    public ConfigAbstract(OMCPlugin plugin) {
        this.plugin = plugin;
    }

    public abstract void initialize();

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

    public static List<String> getStringList(String path) {
        return STRING_LIST_VALUES.getOrDefault(path, ValueDef.EMPTY_LIST);
    }

    public abstract void saveToConfig();

    public FileConfiguration getConfig() {
        return omcConfig.getConfig();
    }

    private ValueBuilder getBuilder() {
        if (this.omcConfig == null)
            this.omcConfig = plugin.getOMCConfig();

        if (this.builder == null)
            this.builder = new ValueBuilder(this.omcConfig, STRING_VALUES, INT_VALUES, BOOL_VALUES, STRING_LIST_VALUES);

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
