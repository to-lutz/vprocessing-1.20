package de.verdox.vprocessing;

import de.verdox.vprocessing.commands.UserCommands;
import de.verdox.vprocessing.configuration.Messages;
import de.verdox.vprocessing.configuration.MySQLConfig;
import de.verdox.vprocessing.configuration.ProcessConfiguration;
import de.verdox.vprocessing.dataconnection.MySQL;
import de.verdox.vprocessing.dataconnection.PlayerSession;
import de.verdox.vprocessing.listener.Listeners;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public class VProcessing extends JavaPlugin {

    public static Plugin plugin;
    public static ProcessConfiguration processConfiguration;
    public static Messages messages;
    public static MySQLConfig mySQLConfig;
    public static MySQL mySQL;

    @Override
    public void onEnable() {
        plugin = this;
        System.out.println("Gestartet");
        processConfiguration = new ProcessConfiguration(this,"processer.yml","V-Processing");
        messages = new Messages(this,"messages.yml","V-Processing");
        mySQLConfig = new MySQLConfig(this,"MySQL.yml","V-Processing");
        mySQL = new MySQL(mySQLConfig.getHost(),mySQLConfig.getPort(),mySQLConfig.getDatabase(),mySQLConfig.getUsername(),mySQLConfig.getPassword());
        try {
            mySQL.connect();
        } catch (SQLException e) {
            plugin.getPluginLoader().disablePlugin(this);
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            plugin.getPluginLoader().disablePlugin(this);
            e.printStackTrace();
        }
        setupCommands();
        setupEventListener();
        Bukkit.getOnlinePlayers().stream().forEach(player -> PlayerSession.getSession(player));
    }

    private void setupCommands(){
        this.getCommand("processer").setExecutor(new UserCommands());
    }
    private void setupEventListener(){
        Bukkit.getPluginManager().registerEvents(new Listeners(),this);
    }
}
