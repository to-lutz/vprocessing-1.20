package de.verdox.vprocessing.model;

import de.verdox.vprocessing.VProcessing;
import de.verdox.vprocessing.configuration.messages.SuccessMessage;
import de.verdox.vprocessing.utils.HiddenStringUtils;
import de.verdox.vprocessing.utils.SecondsConverter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

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
    public static ItemStack process_Button(Processer processer){
        ItemStack stack = new ItemStack(Material.ORANGE_STAINED_GLASS_PANE,1);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',SuccessMessage.GUI_Start_Task.getMessage()));
        if(processer!=null){
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.translateAlternateColorCodes('&',"&e"+processer.getDurationString()));
            lore.add("");
            lore.add(ChatColor.translateAlternateColorCodes('&',SuccessMessage.GUI_NeededItems.getMessage()));
            for(ItemStack s:processer.getRequiredItems()){
                String displayName = s.getItemMeta().getDisplayName();
                if(displayName == null || displayName.equals(""))
                    displayName = s.getType().toString();
                lore.add(ChatColor.translateAlternateColorCodes('&',"&7"+displayName+" &8[&e"+s.getAmount()+"&8]"));
            }
            lore.add("");
            lore.add(ChatColor.translateAlternateColorCodes('&',SuccessMessage.GUI_RewardItems.getMessage()));
            for(ItemStack s:processer.getOutputItems()){
                String displayName = s.getItemMeta().getDisplayName();
                if(displayName == null || displayName.equals(""))
                    displayName = s.getType().toString();
                lore.add(ChatColor.translateAlternateColorCodes('&',"&7"+displayName+" &8[&e"+s.getAmount()+"&8]"));
            }
            meta.setLore(lore);
        }
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
    public static ItemStack activeTaskButton(ProcessTask task){
        ItemStack stack = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE,1);
        ItemMeta meta = stack.getItemMeta();

        float remainingSeconds = Math.abs((System.currentTimeMillis()-task.getTimestampStop())/1000);
        float duration = (task.getTimestampStop()-task.getTimestampStarted())/1000;
        int percentage = (int)((1-(remainingSeconds/duration))*100);

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',SuccessMessage.GUI_Remaining_Time.getMessage()+""+ SecondsConverter.convertSeconds((long)remainingSeconds)));
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.translateAlternateColorCodes('&',"&6"+percentage+"%"));
        meta.setLore(lore);
        stack.setItemMeta(meta);
        return stack;
    }
    public static ItemStack cancelButton(){
        ItemStack stack = new ItemStack(Material.RED_STAINED_GLASS_PANE,1);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',SuccessMessage.GUI_Cancel_Button.getMessage()));
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
    public static ItemStack borderBlack(){
        ItemStack stack = new ItemStack(Material.BLACK_STAINED_GLASS_PANE,1);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',SuccessMessage.GUI_Border.getMessage()));
        stack.setItemMeta(meta);
        return stack;
    }
    public static ItemStack percentage(int percent){
        ItemStack stack = new ItemStack(Material.GREEN_STAINED_GLASS_PANE,1);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&b"+percent+"%"));
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
