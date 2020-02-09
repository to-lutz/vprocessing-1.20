package de.verdox.vprocessing.utils;

import de.verdox.vprocessing.model.Processer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;
import java.util.List;

public class InventoryHandler {
    public static boolean givePlayerProcessedItems(Player p, Processer processer){
        if(p == null)
            return false;
        if(processer == null)
            return false;
        List<ItemStack> processedItems = processer.getOutputItems();
        int slotsNeeded = processedItems.size();
        int counter = 0;
        for(ItemStack s:p.getInventory().getStorageContents()){
            if(s==null)
                counter++;
        }
        if(counter >= slotsNeeded){
            for(ItemStack s:processedItems){
                p.getInventory().addItem(s);
            }
            return true;
        }
        return false;
    }
    public static boolean givePlayerRequiredItems(Player p,Processer processer){
        if(p == null)
            return false;
        if(processer == null)
            return false;
        List<ItemStack> requiredItems = processer.getRequiredItems();
        int slotsNeeded = requiredItems.size();
        int counter = 0;
        for(ItemStack s:p.getInventory().getStorageContents()){
            if(s==null)
                counter++;
        }
        if(counter >= slotsNeeded){
            for(ItemStack s:requiredItems){
                p.getInventory().addItem(s);
            }
            return true;
        }
        return false;
    }


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
        int toTake = stack.getAmount();
        for(int i = 0;i<p.getInventory().getContents().length;i++){
            ItemStack current = p.getInventory().getItem(i);
            if(current!=null && current.isSimilar(stack)){
                if(toTake-current.getAmount() > 0){
                    //System.out.println("Stack ist kleiner: "+toTake+">"+current.getAmount());
                    toTake-=current.getAmount();
                    p.getInventory().setItem(i,null);
                }
                else if(toTake-current.getAmount() < 0){
                    //System.out.println("Stack ist größer: "+toTake+">"+current.getAmount());
                    current.setAmount(current.getAmount()-toTake);
                    break;
                }
                else{
                    //System.out.println("Stack ist gleich: "+toTake+"="+current.getAmount());
                    p.getInventory().setItem(i,null);
                    break;
                }
            }
        }
    }
}
