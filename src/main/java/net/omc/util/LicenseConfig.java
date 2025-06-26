package net.omc.util;

import net.omc.OMCPlugin;
import org.json.JSONObject;

import java.io.*;

public class LicenseConfig {
    private final File file;
    private final Cryptography cryptography;
    private JSONObject data;

    public LicenseConfig(OMCPlugin plugin, String file_name, Cryptography cryptography) {
        this.file = new File(plugin.getDataFolder(), "/lib/" + file_name);
        this.cryptography = cryptography;
    }

    public void loadEncrypted(String cryptKey) throws Exception {
        generateFile();

        String encrypted = load(false);
        String decrypted = cryptography.decrypt(encrypted, cryptKey);

        this.data = new JSONObject(decrypted);
    }

    public void saveEncrypted(String cryptKey) throws Exception {
        generateFile();

        String plain = toJSONString();
        String encrypted = cryptography.encrypt(plain, cryptKey);

        save(encrypted);
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

        if (set)
            this.data = new JSONObject(read.toString());

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
