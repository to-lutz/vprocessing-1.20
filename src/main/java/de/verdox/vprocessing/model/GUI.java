package de.verdox.vprocessing.model;

import de.verdox.vprocessing.VProcessing;
import de.verdox.vprocessing.configuration.messages.SuccessMessage;
import de.verdox.vprocessing.utils.HiddenStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class GUI {
    protected Inventory inv;
    protected String identifier;
    public GUI(int size, String title, String identifier){
        this.inv = Bukkit.createInventory(null,size,ChatColor.translateAlternateColorCodes('&',title));
        this.identifier = identifier;

        for(int i = 0;i<9;i++){
            inv.setItem(i,border());
        }
        for(int i = inv.getSize()-9;i<inv.getSize();i++){
            inv.setItem(i,border());
        }
        inv.setItem(inv.getSize()-1,identifierStack());
        setContent();
        updateInventory();
    }

    protected BukkitRunnable runAsync(final Runnable runnable){
        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        };
        r.runTaskAsynchronously(VProcessing.plugin);
        return r;
    }

    public abstract void setContent();
    public abstract void updateInventory();
    private ItemStack identifierStack(){
        ItemStack stack = new ItemStack(Material.GRAY_STAINED_GLASS_PANE,1);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',SuccessMessage.GUI_Border.getMessage()+HiddenStringUtils.encodeString(identifier)));
        stack.setItemMeta(meta);
        return stack;
    }
    public static ItemStack acceptButton(){
        ItemStack stack = new ItemStack(Material.GREEN_STAINED_GLASS_PANE,1);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',SuccessMessage.GUI_Accept.getMessage()));
        stack.setItemMeta(meta);
        return stack;
    }
    public static ItemStack process_Button(){
        ItemStack stack = new ItemStack(Material.ORANGE_STAINED_GLASS_PANE,1);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',SuccessMessage.GUI_Start_Task.getMessage()));
        stack.setItemMeta(meta);
        return stack;
    }
    public static ItemStack idleButton(){
        ItemStack stack = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE,1);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',SuccessMessage.GUI_Idle.getMessage()));
        stack.setItemMeta(meta);
        return stack;
    }
    public static ItemStack border(){
        ItemStack stack = new ItemStack(Material.GRAY_STAINED_GLASS_PANE,1);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',SuccessMessage.GUI_Border.getMessage()));
        stack.setItemMeta(meta);
        return stack;
    }
    public static boolean hasIdentifier(Inventory inv){
        if(inv == null)
            return false;
        ItemStack stack = inv.getItem(inv.getSize()-1);
        if(stack == null)
            return false;
        ItemMeta meta = stack.getItemMeta();
        if(meta == null)
            return false;
        String displayName = meta.getDisplayName();
        if(displayName == null)
            return false;
        return HiddenStringUtils.hasHiddenString(displayName);
    }
    public static String getIdentifier(Inventory inv){
        if(inv == null)
            return null;
        ItemStack stack = inv.getItem(inv.getSize()-1);
        if(stack == null)
            return null;
        ItemMeta meta = stack.getItemMeta();
        if(meta == null)
            return null;
        String displayName = meta.getDisplayName();
        if(displayName == null)
            return null;
        return HiddenStringUtils.extractHiddenString(displayName);
    }
    public Inventory gui(){return this.inv;}
}
