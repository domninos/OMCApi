package net.omc.database;

import net.omc.OMCPlugin;
import net.omc.util.Libraries;

import java.util.Arrays;

public interface OMCDatabase {
    void close();

    boolean isEnabled();

    String getType();

    enum Type {
        REDIS("redis", Libraries.REDIS, "Redis"),
        FLAT_FILE("flat-file", Libraries.FLAT_FILE, "Flat-File"),
        POSTGRESQL("postgresql", Libraries.POSTGRESQL, "PostgreSQL"),
        SQLITE("sqlite", Libraries.SQLITE, "SQLite");

        private final String label, formal;

        private final Libraries lib;

        Type(String label, Libraries lib, String formal) {
            this.label = label;
            this.lib = lib;
            this.formal = formal;
        }

        public String getLabel() {
            return label;
        }

        public String getFormal() {
            return formal;
        }

        public boolean isLoaded(OMCPlugin plugin) {
            return lib.isLoaded(plugin);
        }

        public static Type parseType(String label) {
            for (Type type : Type.values())
                if (type.getLabel().equalsIgnoreCase(label))
                    return type;

            return null;
        }

        public static String available() {
            return Arrays.toString(OMCDatabase.Type.values())
                    .replace("_", "-").replace("[", "").replace("]", "");
        }
    }
}
