package de.verdox.vprocessing.listener;

import de.verdox.vprocessing.VProcessing;
import de.verdox.vprocessing.configuration.ProcessConfiguration;
import de.verdox.vprocessing.configuration.messages.ErrorMessage;
import de.verdox.vprocessing.configuration.messages.SuccessMessage;
import de.verdox.vprocessing.dataconnection.PlayerSession;
import de.verdox.vprocessing.model.GUI;
import de.verdox.vprocessing.model.ProcessTask;
import de.verdox.vprocessing.model.Processer;
import de.verdox.vprocessing.model.ProcesserGUI;
import de.verdox.vprocessing.utils.InventoryHandler;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;

public class Listeners implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        PlayerSession.addToCache(p);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        Player p = e.getPlayer();
        PlayerSession.removeFromCache(p);
    }

    @EventHandler
    public void blockInteract(PlayerInteractEvent e){
        Block block = e.getClickedBlock();
        Player p = e.getPlayer();
        e.setCancelled(true);
        if(block == null)
            return;
        if(!VProcessing.processConfiguration.locationCache.containsKey(block.getLocation()))
            return;
        Processer processer = VProcessing.processConfiguration.locationCache.get(block.getLocation());
        if(processer == null)
            return;
        p.openInventory(new ProcesserGUI(processer).gui());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        Player p = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();

        if(item == null)
            return;

        // Es handelt sich um ein vom Plugin erstelltes GUI
        if(GUI.hasIdentifier(e.getClickedInventory())){
            e.setCancelled(true);
            p.updateInventory();

            String identifier = GUI.getIdentifier(e.getClickedInventory());
            ProcessConfiguration config = VProcessing.processConfiguration;
            PlayerSession session = PlayerSession.getSession(p);

            // Es handelt sich um einen Processer-Identifier!

            if(identifier.contains("Processer: ")){
                identifier = identifier.replace("Processer: ","");
                if(!config.exist(identifier))
                    throw new IllegalStateException("The given Processer: "+identifier+" does not exist somehow?");
                Processer processer = config.processerCache.get(identifier);

                if(item.equals(GUI.process_Button(processer))){
                    if(session.hasPlayerTask(processer)){
                        p.sendMessage(ErrorMessage.Already_Processing.getMessage());
                        return;
                    }

                    if(!InventoryHandler.hasPlayerRequiredItems(p,processer)){
                        p.sendMessage(ErrorMessage.Not_Enough_Items.getMessage());
                        return;
                    }

                    try {
                        InventoryHandler.takeItems(p,processer);
                        session.createTask(processer);
                        p.sendMessage(SuccessMessage.Process_Begin.getMessage());
                    } catch (SQLException ex) {
                        System.out.println("There was an error creating a new Task!");
                        ex.printStackTrace();
                    }
                }
                else if(item.equals(GUI.acceptButton())){
                    if(session.hasPlayerTask(processer)){
                        ProcessTask task = session.getTask(processer);
                        if(!task.isFinished())
                            return;
                        if(!InventoryHandler.givePlayerProcessedItems(p,processer)){
                            p.sendMessage(ErrorMessage.Not_Enough_InvSpace.getMessage());
                            return;
                        }
                        try {
                            session.removeTask(processer);
                            p.sendMessage(SuccessMessage.Successfully_Processed.getMessage());
                        } catch (SQLException ex) {
                            System.out.println("Error while trying to remove Task from Database!");ex.printStackTrace();}
                    }
                }
                else if(session.hasPlayerTask(processer)){
                    ProcessTask task = session.getTask(processer);
                    if(task.isFinished())
                        return;
                    if(item.equals(GUI.cancelButton())){
                        if(!InventoryHandler.givePlayerRequiredItems(p,processer)){
                            p.sendMessage(ErrorMessage.Not_Enough_InvSpace.getMessage());
                            return;
                        }
                        try {
                            session.removeTask(processer);
                        } catch (SQLException ex) {System.out.println("Error while trying to remove Task from Database!");ex.printStackTrace();}
                    }
                }
            }
        }
    }

}
