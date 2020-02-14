package de.verdox.vprocessing.model;

import de.verdox.vprocessing.VProcessing;
import org.bukkit.Location;

import java.util.List;

public class Category {

    private Location location;
    private String name;
    private final String categoryID;
    private boolean useHologram;
    private boolean useLargeHologram;
    private List<Processer> processerList;

    public Category(String categoryID,String name,Location loc,List<Processer> processerList,boolean useHologram,boolean useLargeHologram){
        this.categoryID = categoryID;
        this.name = name;
        this.location = loc;
        this.processerList = processerList;
        this.useHologram = useHologram;
        this.useLargeHologram = useLargeHologram;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location loc){
        this.location = loc;
    }

    public String getName() {
        return name;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public boolean isUseHologram() {
        return useHologram;
    }

    public boolean isUseLargeHologram() {
        return useLargeHologram;
    }

    public List<Processer> getProcesserList() {
        return processerList;
    }

    public int getGuiSize(){
        int processerCount = getProcesserList().size();
        if(processerCount <=9){
            return 27;
        }
        else if(processerCount <=18){
            return 36;
        }
        else if(processerCount <=27){
            return 45;
        }
        else if(processerCount <=36){
            return 54;
        }
        else {
            VProcessing.consoleMessage("&4Categories can only have a maximum of 36 processers!");
            throw new IllegalStateException("Categories can only have a maximum of 36 processers!");
        }
    }
}
