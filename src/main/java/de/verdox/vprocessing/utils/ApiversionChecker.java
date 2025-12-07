package de.verdox.vprocessing.utils;

import org.bukkit.plugin.Plugin;

public class ApiversionChecker {
    private static String getVersion(Plugin plugin){
        String s = plugin.getServer().getBukkitVersion();
        return s.split("-")[0];
    }
    public static boolean isLegacyVersion(Plugin plugin){
        String version = getVersion(plugin);
        return !version.startsWith("1.20");
    }
}
