package de.verdox.vprocessing.configuration;

import org.bukkit.plugin.Plugin;

public class Settings extends Configuration {
    public Settings(Plugin plugin, String fileName, String pluginDirectory) {
        super(plugin, fileName, pluginDirectory);
    }

    @Override
    void setupConfig() {
        config.options().header("General Settings");
        config.addDefault("SETTINGS.UseMySQL",true);
        config.addDefault("SETTINGS.UseHolographicDisplays",true);
        config.options().copyDefaults(true);
        save();
    }

    public boolean useMySQL(){return config.getBoolean("SETTINGS.UseMySQL");}
    public boolean useHolograms(){return config.getBoolean("SETTINGS.UseHolographicDisplays");}
}
