package de.verdox.vprocessing.configuration.messages;

import de.verdox.vprocessing.VProcessing;
import org.bukkit.ChatColor;

public enum ErrorMessage {
    No_Processer("Processer does not exist!"),
    Not_Enough_Items("&cYou don't have the required Items!"),
    Already_Processing("&cYou are already processing here!"),
    ;

    private String message;
    ErrorMessage(String msg){
        this.message = msg;
    }

    public String getDefaultMessage(){return this.message;}
    public String getMessage(){return ChatColor.translateAlternateColorCodes('&',VProcessing.messages.getMessage("Error_Messages."+this.name()));}
}
