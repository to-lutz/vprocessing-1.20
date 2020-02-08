package de.verdox.vprocessing.commands;

import de.verdox.vprocessing.VProcessing;
import de.verdox.vprocessing.configuration.messages.ErrorMessage;
import de.verdox.vprocessing.model.Processer;
import de.verdox.vprocessing.model.ProcesserGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UserCommands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length == 2){
            if(args[0].equals("open")){
                String processerID = args[1];
                if(!VProcessing.processConfiguration.exist(processerID)){
                    sender.sendMessage(ErrorMessage.No_Processer.getMessage());
                    return false;
                }
                if(!(sender instanceof Player))
                    return false;
                Player p = (Player) sender;
                Processer processer = VProcessing.processConfiguration.processerCache.get(processerID);
                p.openInventory(new ProcesserGUI(processer).gui());
            }
        }
        return false;
    }
}