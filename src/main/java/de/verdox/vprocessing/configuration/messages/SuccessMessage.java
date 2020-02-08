package de.verdox.vprocessing.configuration.messages;

import de.verdox.vprocessing.VProcessing;
import org.bukkit.ChatColor;

public enum SuccessMessage {

    Successfully_Command("Successfully done!"),
    Successfully_loaded_files("Successfully loaded your Playerfiles from the database!"),
    Successfully_Processed("&aProcessed Items successfully!"),
    Process_Begin("&aBeginning processing your items!"),
    GUI_Start_Task("&6Process Items"),
    GUI_Accept("&aTake processed items"),
    GUI_Idle("&eAlready Processing"),
    GUI_Border("&8*"),
    ;

    private String message;
    SuccessMessage(String msg){
        this.message = msg;
    }

    public String getDefaultMessage(){return this.message;}
    public String getMessage(){return ChatColor.translateAlternateColorCodes('&',VProcessing.messages.getMessage("Success_Messages."+this.name()));}
}
