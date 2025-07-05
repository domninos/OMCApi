package net.omc.config;

import net.omc.OMCPlugin;
import org.json.JSONObject;

import java.io.*;

public class LicenseConfig {
    private final File file;
    private JSONObject data;

    public LicenseConfig(OMCPlugin plugin, String file_name) {
        this.file = new File(plugin.getDataFolder(), "/lib/" + file_name);
    }

    public void save() {
        save(data.toString());
    }

    public void save(String toSave) {
        generateFile();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(toSave);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String load(boolean set) throws IOException {
        generateFile();

        StringBuilder read = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                read.append(line);
            }
        }

        if (!read.isEmpty() && set)
            this.data = new JSONObject(read.toString());
        else
            this.data = new JSONObject();

        return read.toString();
    }

    public boolean contains(String key) {
        return data.has(key);
    }

    public String get(String key) {
        return contains(key) ? data.getString(key) : "NULL";
    }

    public long getLong(String key) {
        return contains(key) ? data.getLong(key) : -1;
    }

    public int getInt(String key) {
        return contains(key) ? data.getInt(key) : -1;
    }

    public void set(String key, Object value) {
        data.put(key, value);
    }

    public String toJSONString() {
        return data.toString(2);
    }

    private void generateFile() {
        try {
            if (!file.exists())
                file.createNewFile();
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }
}
