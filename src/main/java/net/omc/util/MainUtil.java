package net.omc.util;

import org.bukkit.Bukkit;

public class MainUtil {
    public static int VERSION;
    public static String FULL_VERSION;

    static {
        String major = Bukkit.getServer().getBukkitVersion().split("-")[0];

        String[] majorSplit = major.split("\\."); // 1.21

        FULL_VERSION = major;
        VERSION = Integer.parseInt(majorSplit[1]); // 21
    }
}
