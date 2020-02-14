package de.verdox.vprocessing.commands;

import de.verdox.vprocessing.VProcessing;
import de.verdox.vprocessing.configuration.messages.ErrorMessage;
import de.verdox.vprocessing.configuration.messages.SuccessMessage;
import de.verdox.vprocessing.model.Category;
import de.verdox.vprocessing.model.CategoryGUI;
import de.verdox.vprocessing.model.Processer;
import de.verdox.vprocessing.model.ProcesserGUI;
import de.verdox.vprocessing.utils.ApiversionChecker;
import de.verdox.vprocessing.utils.InventoryHandler;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

public class AdminCommands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length == 1){
            if(args[0].equalsIgnoreCase("list")){
                for(String processerID : VProcessing.processConfiguration.processerCache.keySet()){
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&8- &b"+processerID));
                }
            }
            else if(args[0].equalsIgnoreCase("help")){
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&8]======[&4V-Processing&8]======["));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&7/&evproc &8<&aprocesser&7/&acategory&8> &bgive &8- &6Gives you required Items for the processer&7!"));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&7/&evproc &8<&aprocesser&7/&acategory&8> &bset &8- &6Sets the position of the processer&7!"));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&7/&evproc &8<&aprocesser&7/&acategory&8> &bopen &8- &6Opens the Processer&7!"));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&7/&evproc &blist &8- &6Lists up all processers&7!"));
            }
        }
        else if(args.length == 2){
            String ID = args[0];

            // Open GUI of Processer / Category

            if(args[1].equalsIgnoreCase("open")){
                if(!(sender instanceof Player))
                    return false;
                Player p = (Player) sender;

                if(VProcessing.processConfiguration.exist(ID)){
                    Processer processer = VProcessing.processConfiguration.processerCache.get(ID);
                    p.openInventory(new ProcesserGUI(processer).gui());
                    return true;
                }
                else if(VProcessing.categoryConfiguration.exist(ID)){
                    Category category = VProcessing.categoryConfiguration.categoryCache.get(ID);
                    p.openInventory(new CategoryGUI(category).gui());
                    return true;
                }
                else {
                    p.sendMessage(ErrorMessage.No_Processer_or_Category.getMessage());
                    return false;
                }
            }

            // Set Location of Processer / Category

            else if(args[1].equalsIgnoreCase("set")){
                if(!(sender instanceof Player))
                    return false;
                Player p = (Player) sender;
                Location newLoc;

                if(ApiversionChecker.isLegacyVersion(VProcessing.plugin)){
                   Block block =  p.getTargetBlock(null,5);
                   if(block == null){
                       p.sendMessage(ErrorMessage.Look_At_Block.getMessage());
                       return false;
                   }
                   newLoc = block.getLocation();
                }
                else {
                    RayTraceResult result = p.rayTraceBlocks(5);
                    if(result==null){
                        p.sendMessage(ErrorMessage.Look_At_Block.getMessage());
                        return false;
                    }

                    newLoc = result.getHitBlock().getLocation();
                }

                if(VProcessing.processConfiguration.exist(ID)){
                    if(VProcessing.categoryConfiguration.locationCache.containsKey(newLoc)){
                        p.sendMessage(ErrorMessage.Loc_Already_Used.getMessage());
                        return false;
                    }

                    if(VProcessing.processConfiguration.changeLocation(ID,newLoc)){
                        p.sendMessage(SuccessMessage.Successfully_Changed_Loc.getMessage());
                    }
                    else {
                        p.sendMessage(ErrorMessage.Loc_Already_Used.getMessage());
                    }

                }
                else if(VProcessing.categoryConfiguration.exist(ID)){
                    if(VProcessing.processConfiguration.locationCache.containsKey(newLoc)){
                        p.sendMessage(ErrorMessage.Loc_Already_Used.getMessage());
                        return false;
                    }


                    if(VProcessing.categoryConfiguration.changeLocation(ID,newLoc)){
                        p.sendMessage(SuccessMessage.Successfully_Changed_Loc.getMessage());
                    }
                    else {
                        p.sendMessage(ErrorMessage.Loc_Already_Used.getMessage());
                    }
                }
                else {
                    p.sendMessage(ErrorMessage.No_Processer_or_Category.getMessage());
                }
            }

            else if(args[1].equalsIgnoreCase("get")){
                if(!(sender instanceof Player))
                    return false;
                if(VProcessing.processConfiguration.exist(ID))
                    sender.sendMessage(ErrorMessage.No_Processer.getMessage());
                Processer processer = VProcessing.processConfiguration.processerCache.get(ID);
                Player p = (Player) sender;
                InventoryHandler.givePlayerRequiredItems(p,processer);
                p.sendMessage(SuccessMessage.Successfully_Command.getMessage());
            }
        }
        return false;
    }
}
