package de.verdox.vprocessing.configuration;

import de.verdox.vprocessing.model.Processer;
import de.verdox.vprocessing.utils.Serializer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessConfiguration extends Configuration{

    public Map<String,Processer> processerCache;
    public Map<Location, Processer> locationCache;

    public ProcessConfiguration(Plugin plugin,String fileName, String pluginDirectory){
        super(plugin,fileName,pluginDirectory);
        locationCache = new HashMap<>();
        initProcesserCache();
    }

    public boolean exist(String processerID){
        if(processerID == null)
            return false;
        return processerCache.containsKey(processerID);
    }

    public boolean changeLocation (String processerID, Location loc){
        if(processerID == null)
            return false;
        if(!processerCache.containsKey(processerID))
            return false;
        if(locationCache.containsKey(loc))
            return false;
        Location oldLoc = processerCache.get(processerID).getLocation();
        // Remove from Locationcache
        if(oldLoc!=null)
            locationCache.remove(oldLoc);

        if(!changeLoc(processerID,loc))
            return false;
        processerCache.get(processerID).setLocation(loc);
        locationCache.put(loc,processerCache.get(processerID));
        return true;
    }

    private void initProcesserCache(){
        processerCache = new HashMap<>();
        for(String processerID:config.getConfigurationSection(configSections.PROCESSER.name()).getKeys(false)){
            Processer processer = getProcesser(processerID);
            if(processer!=null){
                processerCache.put(processer.getProcesserID(),processer);
                if(processer.getLocation()!=null){
                    if(processerCache.containsKey(processer.getLocation()))
                        throw new IllegalStateException("Processer need unique locations! They can't share the same location");
                    locationCache.put(processer.getLocation(),processer);
                }
            }
        }
    }

    @Override
    void setupConfig() {
        if(!file.exists()){
            config.options().header("Config to setup processers, amounts etc...");
            config.addDefault(configSections.PROCESSER+".processer_1."+configSections.PROCESSER_NAME,"&aMühle");
            config.addDefault(configSections.PROCESSER+".processer_1."+configSections.DURATION,60);
            config.addDefault(configSections.PROCESSER+".processer_1."+configSections.required_items+".item_1."+configSections.DISPLAY_NAME,"&7Mehl");
            config.addDefault(configSections.PROCESSER+".processer_1."+configSections.required_items+".item_1."+configSections.ID,"BONE_MEAL");
            config.addDefault(configSections.PROCESSER+".processer_1."+configSections.required_items+".item_1."+configSections.AMOUNT,20);

            config.addDefault(configSections.PROCESSER+".processer_1."+configSections.processed_items+".item_1."+configSections.DISPLAY_NAME,"&7Mehl");
            config.addDefault(configSections.PROCESSER+".processer_1."+configSections.processed_items+".item_1."+configSections.ID,"BONE_MEAL");
            config.addDefault(configSections.PROCESSER+".processer_1."+configSections.processed_items+".item_1."+configSections.AMOUNT,20);
            config.addDefault(configSections.PROCESSER+".processer_1."+configSections.LOCATION,"none");

            config.options().copyDefaults(true);
            save();
        }
    }

    private boolean changeLoc(String processerID, Location loc){
        if(!exist(processerID))
            return false;
        config.set(configSections.PROCESSER.name()+"."+processerID+"."+configSections.LOCATION,Serializer.serializeLocation(loc));
        return true;
    }

    private Processer getProcesser (String processerID){

        if(!config.isSet(configSections.PROCESSER.name()+"."+processerID)){
            throw new IllegalArgumentException("Processer couldn't be found in config!");
        }
        if(!config.isConfigurationSection(configSections.PROCESSER.name()+"."+processerID))
            throw new IllegalArgumentException("Processer couldn't be found in config!");

        ConfigurationSection section = config.getConfigurationSection(configSections.PROCESSER.name()+"."+processerID);
        String name = section.getString(configSections.PROCESSER_NAME.name());
        int duration = section.getInt(configSections.DURATION.name());
        Location loc = Serializer.deserializeLocation((section.getString(configSections.LOCATION.name())));

        List<ItemStack> requiredItems = new ArrayList<>();
        List<ItemStack> processedItems = new ArrayList<>();

        for(String key:config.getConfigurationSection(section.getCurrentPath()+"."+configSections.required_items).getKeys(false)){
            ConfigurationSection item = config.getConfigurationSection(section.getCurrentPath()+"."+configSections.required_items+"."+key);
            String displayName = config.getString(item.getCurrentPath()+"."+configSections.DISPLAY_NAME);
            String ID = config.getString(item.getCurrentPath()+"."+configSections.ID);
            int amount = config.getInt(item.getCurrentPath()+"."+configSections.AMOUNT);

            ItemStack stack = new ItemStack(Material.getMaterial(ID),amount);
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(displayName);
            stack.setItemMeta(meta);
            requiredItems.add(stack);
        }

        for(String key:config.getConfigurationSection(section.getCurrentPath()+"."+configSections.processed_items).getKeys(false)){
            ConfigurationSection item = config.getConfigurationSection(section.getCurrentPath()+"."+configSections.required_items+"."+key);
            String displayName = config.getString(item.getCurrentPath()+"."+configSections.DISPLAY_NAME);
            String ID = config.getString(item.getCurrentPath()+"."+configSections.ID);
            int amount = config.getInt(item.getCurrentPath()+"."+configSections.AMOUNT);

            ItemStack stack = new ItemStack(Material.getMaterial(ID),amount);
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(displayName);
            stack.setItemMeta(meta);
            processedItems.add(stack);
        }
        return new Processer(processerID,name,duration,loc,requiredItems,processedItems);
    }

    enum configSections{
        PROCESSER,
        required_items,
        processed_items,
        DISPLAY_NAME,
        ID,
        AMOUNT,
        DURATION,
        PROCESSER_NAME,
        LOCATION,
    }
}