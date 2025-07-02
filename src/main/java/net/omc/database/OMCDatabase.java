package net.omc.database;

import net.omc.OMCPlugin;
import net.omc.util.Libraries;

import java.util.Arrays;

public interface OMCDatabase {
    void close();

    boolean isEnabled();

    String getType();

    enum Type {
        REDIS("redis", Libraries.REDIS),
        FLAT_FILE("flat-file", Libraries.FLAT_FILE),
        POSTGRESQL("postgresql", Libraries.POSTGRESQL),
        SQLITE("sqlite", Libraries.SQLITE);

        private final String label;

        private final Libraries lib;

        Type(String label, Libraries lib) {
            this.label = label;
            this.lib = lib;
        }

        public String getLabel() {
            return label;
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
