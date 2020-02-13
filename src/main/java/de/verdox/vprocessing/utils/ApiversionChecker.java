package de.verdox.vprocessing.utils;

import org.bukkit.plugin.Plugin;

public class ApiversionChecker {
    private static String getVersion(Plugin plugin){
        String s = plugin.getServer().getBukkitVersion();
        return s.split("-")[0];
    }
    public static boolean isLegacyVersion(Plugin plugin){
        String version = getVersion(plugin);
        if(version.startsWith("1.15"))
            return false;
        else if(version.startsWith("1.14"))
            return false;
        else if(version.startsWith("1.13"))
            return false;
        else
            return true;
    }
}
