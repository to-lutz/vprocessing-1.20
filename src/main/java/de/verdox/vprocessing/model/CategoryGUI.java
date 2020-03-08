package de.verdox.vprocessing.model;

import de.verdox.vprocessing.VProcessing;
import de.verdox.vprocessing.configuration.messages.SuccessMessage;
import de.verdox.vprocessing.dataconnection.PlayerSession;
import de.verdox.vprocessing.utils.SecondsConverter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryGUI extends GUI {
    public static String identifier = "Category: ";
    private Category category;
    private Map<Processer,Integer> processerItemStackMap;
    public CategoryGUI(Category category) {
        super(category.getGuiSize(), ChatColor.translateAlternateColorCodes('&',category.getName()), identifier+category.getCategoryID(),category.getCategoryID());

    }

    @Override
    public void setContent() {
        category = VProcessing.categoryConfiguration.categoryCache.get(ID);
        processerItemStackMap = new HashMap<>();
        // Upper Border
        for(int i = 0;i<=9;i++)
            gui().setItem(i,GUI.border());
        // Under Border
        for(int i = gui().getSize()-9; i<= gui().getSize()-1;i++)
            gui().setItem(i,GUI.border());
        // Side Border right
        for(int i = 1;i<gui().getSize();i+=8)
            gui().setItem(i,GUI.border());

        for(Processer proc:category.getProcesserList()){
            ItemStack s = proc.getGuiIcon();
            int slot = gui().firstEmpty();
            gui().setItem(slot,s);
            processerItemStackMap.put(proc,slot);
        }
        while(gui().firstEmpty()!=-1){
            gui().setItem(gui().firstEmpty(),GUI.emptySlot());
        }
    }

    @Override
    public void updateInventory() {
        new BukkitRunnable() {
            PlayerSession session;
            @Override
            public void run() {
                if(session==null&&inv.getViewers().size()>0){
                    Player p = (Player) inv.getViewers().get(0);
                    session = PlayerSession.getSession(p);
                }
                if(inv.getViewers().size()==0)
                    this.cancel();
                else {
                    for(Processer processer:processerItemStackMap.keySet()){
                        ItemStack stack = gui().getItem(processerItemStackMap.get(processer));
                        ItemMeta meta = stack.getItemMeta();
                        List<String> lore = new ArrayList<>();
                        if(session.hasPlayerTask(processer)){
                            ProcessTask task = session.getTask(processer);
                            float differemce = task.getTimeDifference();
                            int percentage = task.getPercentage();

                            String timeItTakes = SecondsConverter.convertSeconds((long)differemce);
                            if(task.isFinished())
                                lore.add(ChatColor.translateAlternateColorCodes('&',SuccessMessage.GUI_Finished.getMessage()));
                            else {
                                lore.add(ChatColor.translateAlternateColorCodes('&',"&e"+timeItTakes));
                                lore.add(ChatColor.translateAlternateColorCodes('&',"&6"+percentage+"%"));
                            }
                        }
                        else {
                            lore.add(ChatColor.translateAlternateColorCodes('&', SuccessMessage.GUI_Ready.getMessage()));
                        }
                        meta.setLore(lore);
                        stack.setItemMeta(meta);
                    }
                }
            }
        }.runTaskTimerAsynchronously(VProcessing.plugin,0L,20L);
    }
}
