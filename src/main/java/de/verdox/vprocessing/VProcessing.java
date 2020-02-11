package de.verdox.vprocessing;

import de.verdox.vprocessing.commands.AdminCommands;
import de.verdox.vprocessing.configuration.Messages;
import de.verdox.vprocessing.configuration.MySQLConfig;
import de.verdox.vprocessing.configuration.ProcessConfiguration;
import de.verdox.vprocessing.configuration.Settings;
import de.verdox.vprocessing.dataconnection.DataConnectionImpl;
import de.verdox.vprocessing.dataconnection.MySQL;
import de.verdox.vprocessing.dataconnection.PlayerSession;
import de.verdox.vprocessing.dataconnection.SQLite;
import de.verdox.vprocessing.listener.Listeners;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public class VProcessing extends JavaPlugin {

    public static Plugin plugin;
    public static ProcessConfiguration processConfiguration;
    public static Messages messages;
    public static MySQLConfig mySQLConfig;
    public static Settings settings;
    public static DataConnectionImpl dataConnection;

    @Override
    public void onEnable() {
        plugin = this;
        System.out.println("Gestartet");
        settings = new Settings(this,"settings.yml","V-Processing");
        messages = new Messages(this,"messages.yml","V-Processing");
        processConfiguration = new ProcessConfiguration(this,"processer.yml","V-Processing");
        mySQLConfig = new MySQLConfig(this,"MySQL.yml","V-Processing");
        if(settings.useMySQL()){
            dataConnection = new MySQL(mySQLConfig.getHost(),mySQLConfig.getPort(),mySQLConfig.getDatabase(),mySQLConfig.getUsername(),mySQLConfig.getPassword());
        }
        else {
            dataConnection = new SQLite(this,"v-Processing","V-processing");
        }
        connectDatabase();
        setupCommands();
        setupEventListener();
        Bukkit.getOnlinePlayers().stream().forEach(player -> PlayerSession.getSession(player));
        checkSoftDependency();
    }
    private void connectDatabase(){
        try {
            dataConnection.connect();
        } catch (SQLException e) {
            this.setEnabled(false);
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            this.setEnabled(false);
            e.printStackTrace();
        }
    }

    private void checkSoftDependency(){
        if(!settings.useHolograms())
            return;
        if(!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")){
            System.out.println("UseHolograms activated in settings");
            System.out.println("But Plugin was not found!");
            System.out.println("Plugin will be disabled");
            this.setEnabled(false);
        }
    }
    private void setupCommands(){
        this.getCommand("vproc").setExecutor(new AdminCommands());
    }
    private void setupEventListener(){
        Bukkit.getPluginManager().registerEvents(new Listeners(),this);
    }
}
