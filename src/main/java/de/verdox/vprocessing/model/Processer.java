package de.verdox.vprocessing.model;

import de.verdox.vprocessing.utils.Serializer;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Processer {

    private Location location;
    private List<ItemStack> requiredItems;
    private List<ItemStack> outputItems;
    private String name;
    private int duration;
    private final String processerID;

    public Processer(String processerID,String name,int duration,Location loc, List<ItemStack> rI, List<ItemStack> oI){
        this.processerID = processerID;
        this.name = name;
        this.duration = duration;
        this.location = loc;
        this.requiredItems = rI;
        this.outputItems = oI;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ItemStack> getOutputItems() {return outputItems;}

    public void setOutputItems(List<ItemStack> outputItems) {
        this.outputItems = outputItems;
    }

    public List<ItemStack> getRequiredItems() {
        return requiredItems;
    }

    public void setRequiredItems(List<ItemStack> requiredItems) {
        this.requiredItems = requiredItems;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getProcesserID() {return processerID;}

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(processerID+"[Name: "+name+", Duration: "+duration+", Location: "+ Serializer.serializeLocation(location)+"]\n");
        sb.append("Required Items: ");
        for(ItemStack i: requiredItems){
            sb.append("["+i.getItemMeta().getDisplayName()+", "+i.getType()+", "+i.getAmount()+"}, ");
        }
        sb.append("\nOutput Items: ");
        for(ItemStack i: outputItems){
            sb.append("["+i.getItemMeta().getDisplayName()+", "+i.getType()+", "+i.getAmount()+"}, ");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;
        if(!(obj instanceof Processer)){return false;}
        Processer other = (Processer) obj;
        return other.processerID.equals(this.processerID);
    }
}