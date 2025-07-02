package net.omc.database.flatfile;

import net.omc.OMCPlugin;
import net.omc.database.OMCDatabase;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class OMCFlatFileDatabase implements OMCDatabase {

    private final File file;
    private final String fileName;
    private Map<String, Boolean> cache;  // boilerplate
    private boolean enabled = false;

    private final OMCPlugin plugin;


    public OMCFlatFileDatabase(OMCPlugin plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
        this.file = new File(plugin.getDataFolder().getPath() + "/" + fileName);
    }

    public void checkFile() {
        try {
            if (file.createNewFile())
                plugin.sendConsole("&7Created " + fileName);
        } catch (IOException e) {
            plugin.error("Could not initialize database", e);
        }
    }

    public boolean connect() {
        this.cache = readFile();
        this.enabled = true;
        return true;
    }

    @Override
    public void close() {
        cache.clear();
        this.enabled = false;
    }

    public void saveMap(Map<String, Boolean> enabledPlayers) {
        if (enabledPlayers.isEmpty())
            return;

        StringBuilder toSave = new StringBuilder();

        for (Map.Entry<String, Boolean> entry : enabledPlayers.entrySet()) {
            String name = entry.getKey();
            Boolean value = entry.getValue();

            // new entry/player
            toSave.append(name).append(": ").append(value.toString()).append("\n");
        }

        writeToFile(toSave.toString(), false);
    }

    public void savePlayer(String playerName, boolean value) {
        Map<String, Boolean> savedPlayers = readFile();

        if (savedPlayers.containsKey(playerName))
            savedPlayers.replace(playerName, value); // in database already, replace
        else
            savedPlayers.put(playerName, value); // new to database

        saveMap(savedPlayers);

        savedPlayers.clear(); // garbage dump
    }

    public boolean has(String playerName) {
        return cache.containsKey(playerName);
    }


    public boolean isEnabled() {
        return this.enabled && cache != null;
    }

    @Override
    public String getType() {
        return "FLAT-FILE";
    }

    public boolean getValue(String playerName) {
        return cache.getOrDefault(playerName, false);
    }

    public Map<String, Boolean> getCache() {
        return cache;
    }

    // resource heavy function
    public Map<String, Boolean> readFile() {
        checkFile();

        Map<String, Boolean> enabled = new HashMap<>();

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if (line.isBlank())
                    continue;

                String[] split = line.split(":");

                // name: boolean
                String name = split[0];
                boolean value = Boolean.parseBoolean(split[1].strip());

                enabled.put(name, value);
            }
        } catch (IOException e) {
            plugin.error("Something went wrong reading file: ", e);
        }

        return enabled;
    }

    public void writeToFile(String line, boolean append) {
        try (FileWriter writer = new FileWriter(file, append)) {
            writer.write(line + "\n");
        } catch (IOException e) {
            plugin.error("Something went wrong writing to file: ", e);
        }
    }
}
