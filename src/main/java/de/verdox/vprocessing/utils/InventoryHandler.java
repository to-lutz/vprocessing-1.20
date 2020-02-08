package de.verdox.vprocessing.utils;

import de.verdox.vprocessing.model.Processer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Iterator;

public class InventoryHandler {
    public static boolean hasPlayerRequiredItems(Player p, Processer processer){
        if(p == null)
            return false;
        if(processer == null)
            return false;
        Iterator<ItemStack> it = processer.getRequiredItems().iterator();
        while(it.hasNext()){
            ItemStack stack = it.next();
            ItemStack comparer = new ItemStack(stack.getType(),1);
            comparer.setItemMeta(stack.getItemMeta());
            if(!p.getInventory().containsAtLeast(comparer,stack.getAmount()))
                return false;
        }
        return true;
    }
    public static void takeItems(Player p,Processer processer){
        if(p == null)
            return;
        if(processer == null)
            return;
        Iterator<ItemStack> it = processer.getRequiredItems().iterator();
        while(it.hasNext()){
            ItemStack stack = it.next();
            takeItem(p,stack);
        }
    }
    public static void takeItem(Player p,ItemStack stack){
        PlayerInventory inv = p.getInventory();
        int toTake = stack.getAmount();
        for(int i = 0;i<inv.getContents().length;i++){
            ItemStack s = inv.getItem(i);
            if(toTake > 0){
                if(s==null)
                    continue;
                if(s.getAmount() <= toTake){
                    toTake-=s.getAmount();
                    inv.remove(s);
                }
                else{
                    inv.setItem(i,new ItemStack(s.getType(),s.getAmount()-toTake));
                    toTake-=s.getAmount();
                }
            }
        }
    }
}
