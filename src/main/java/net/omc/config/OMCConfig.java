package net.omc.config;

import net.omc.OMCPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class OMCConfig {

    private final OMCPlugin plugin;
    private final File file;
    private final boolean res;
    private final String fileName;
    private FileConfiguration config;

    public OMCConfig(OMCPlugin plugin, String fileName) {
        this(plugin, fileName, plugin.getDataFolder(), false);
    }

    public OMCConfig(OMCPlugin plugin, String fileName, boolean res) {
        this(plugin, fileName, plugin.getDataFolder(), res);
    }

    public OMCConfig(OMCPlugin plugin, String fileName, File directory) {
        this(plugin, fileName, directory, false);
    }

    public OMCConfig(OMCPlugin plugin, String fileName, File directory, boolean res) {
        this.plugin = plugin;

        if (!fileName.endsWith(".yml"))
            fileName += ".yml";

        this.file = new File(directory, fileName);
        this.fileName = fileName;
        this.res = res;

        reload();
    }

    public void set(String path, Object obj) {
        set(path, obj, true);
    }

    public void set(String path, Object obj, boolean save) {
        config.set(path, obj);

        if (save)
            save();
    }

    public void setNoSave(String path, Object obj) {
        if (path == null || obj == null)
            return;

        set(path, obj, false);
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.error("Something went wrong saving " + file.getName(), e);
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public String getString(String path) {
        return config.getString(path);
    }

    public int getInt(String path) {
        return config.getInt(path);
    }

    public boolean getBool(String path) {
        return config.getBoolean(path, false);
    }

    public void reload() {
        if (!file.exists()) {
            if (res) {
                plugin.saveResource(fileName, false);
            } else {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    plugin.error("Something went wrong creating " + fileName, e);
                }
            }
        }

        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public OMCPlugin getPlugin() {
        return plugin;
    }
}