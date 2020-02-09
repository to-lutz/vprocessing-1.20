package de.verdox.vprocessing.commands;

import de.verdox.vprocessing.VProcessing;
import de.verdox.vprocessing.configuration.messages.ErrorMessage;
import de.verdox.vprocessing.model.Processer;
import de.verdox.vprocessing.model.ProcesserGUI;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

public class AdminCommands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length == 2){
            String processerID = args[0];
            if(!VProcessing.processConfiguration.exist(processerID)){
                sender.sendMessage(ErrorMessage.No_Processer.getMessage());
                return false;
            }
            Processer processer = VProcessing.processConfiguration.processerCache.get(processerID);
            if(args[1].equalsIgnoreCase("open")){
                if(!(sender instanceof Player))
                    return false;
                Player p = (Player) sender;

                p.openInventory(new ProcesserGUI(processer).gui());
                return true;
            }
            if(args[1].equalsIgnoreCase("set")){
                if(!(sender instanceof Player))
                    return false;
                Player p = (Player) sender;
                RayTraceResult result = p.rayTraceBlocks(5);
                if(result==null){
                    p.sendMessage("You are not looking at a Block, maybe air?");
                    return false;
                }

                Location newLoc = result.getHitBlock().getLocation();

                if(VProcessing.processConfiguration.changeLocation(processerID,newLoc)){
                    p.sendMessage("Successfully changed Location!");
                }
                else {
                    p.sendMessage("Location already in use or null!");
                }

                return true;
            }
        }
        return false;
    }
}
