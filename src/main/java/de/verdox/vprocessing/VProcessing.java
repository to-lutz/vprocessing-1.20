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
import de.verdox.vprocessing.utils.ApiversionChecker;
import de.verdox.vprocessing.utils.UpdateChecker;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;

public class VProcessing extends JavaPlugin {

    public static Plugin plugin;
    public static ProcessConfiguration processConfiguration;
    public static Messages messages;
    public static MySQLConfig mySQLConfig;
    public static Settings settings;
    public static DataConnectionImpl dataConnection;
    private static int bStatsID = 6476;
    private static int spigotID = 75112;
    private static String PluginName = "V-Processing";

    @Override
    public void onEnable() {
        if(ApiversionChecker.isLegacyVersion(this))
            consoleMessage("&eFound Spigot Legacy Version&7: < &b1.13");
        else
            consoleMessage("&eFound new Spigot Version&7: > &b1.12");
        plugin = this;

        settings = new Settings(this,"settings.yml","V-Processing/settings");
        if(!checkSoftDependency()){
            this.setEnabled(false);
            return;
        }
        messages = new Messages(this,"messages.yml","V-Processing/settings");
        mySQLConfig = new MySQLConfig(this,"MySQL.yml","V-Processing/dataconnection");
        processConfiguration = new ProcessConfiguration(this,"processer.yml","V-Processing/ingame");
        if(settings.useMySQL()){
            dataConnection = new MySQL(mySQLConfig.getHost(),mySQLConfig.getPort(),mySQLConfig.getDatabase(),mySQLConfig.getUsername(),mySQLConfig.getPassword());
        }
        else {
            dataConnection = new SQLite(this,"v-Processing","V-processing/dataconnection");
        }
        connectDatabase();
        setupCommands();
        setupEventListener();
        Bukkit.getOnlinePlayers().stream().forEach(player -> PlayerSession.getSession(player));

        initBStats();

        new UpdateChecker(this, spigotID).getVersion(version -> {
            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                consoleMessage("&aThere is no new Update available.");
            } else {
                if(settings.useUpdateChecker()){
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Bukkit.getOnlinePlayers().stream().forEach(player -> {
                                versionWhisper(player,version);
                            });
                        }
                    }.runTaskTimerAsynchronously(this,0L,300L*20);
                    consoleMessage("&eThere is a new Update available. ");
                    consoleMessage("&eCurrent Version&7: &c"+this.getDescription().getVersion());
                    consoleMessage("&eLatest Version&7: &b"+version);
                    consoleMessage("&eDownload at: &bhttps://www.spigotmc.org/resources/v-processing-create-processers-and-productioncycles-gui-based-1-13-x-1-15-x-mysql-sqlite.75112/");
                }
            }
        });

        VProcessing.consoleMessage("&aPlugin loaded successfully!");
    }

    private void initBStats(){
        Metrics metrics = new Metrics(this, bStatsID);
        metrics.addCustomChart(new Metrics.SingleLineChart("Processernumber", () -> processConfiguration.processerCache.keySet().size()));
        consoleMessage("&bLoaded bStats successfully!");
    }

    public static void consoleMessage(String message){
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',"&8[&c"+PluginName+"&8] "+message));
    }

    private void connectDatabase(){
        try {
            dataConnection.connect();
        } catch (SQLException e) {
            VProcessing.consoleMessage("&4Connection couldn't be established! "+e.getMessage());
            e.printStackTrace();
            this.setEnabled(false);
        } catch (ClassNotFoundException e) {
            VProcessing.consoleMessage("&4JDBC Driver couldn't be found! "+e.getMessage());
            e.printStackTrace();
            this.setEnabled(false);
        }
    }

    private void versionWhisper(Player p, String version){
        if(p.isOp() || p.hasPermission("vproc.admin")){
            p.sendMessage(ChatColor.translateAlternateColorCodes('&',"&8]======[&4V-Processing&8]======["));
            p.sendMessage(ChatColor.translateAlternateColorCodes('&',"&8|    &eThere is a new Update available. "));
            p.sendMessage(ChatColor.translateAlternateColorCodes('&',"&8|    &bCurrent Version: &c"+this.getDescription().getVersion()));
            p.sendMessage(ChatColor.translateAlternateColorCodes('&',"&8|    &bLatest Version: &a"+version));
            p.sendMessage(ChatColor.translateAlternateColorCodes('&',"&8|    &eDownload at: &bhttps://www.spigotmc.org/resources/v-processing-create-processers-and-productioncycles-gui-based-1-13-x-1-15-x-mysql-sqlite.75112/"));
            p.sendMessage("");
        }
    }

    private boolean checkSoftDependency(){
        if(!settings.useHolograms())
            return true;
        if(!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")){
            Plugin[] pl = Bukkit.getPluginManager().getPlugins();
            consoleMessage("&ePlugin &bHolographicDisplays &ewas not found!");
            consoleMessage("&eFound Plugins&7: ");
            StringBuilder sb = new StringBuilder();
            for(int i = 0;i<pl.length-1;i++){
                sb.append(ChatColor.translateAlternateColorCodes('&',"&e"+pl[i]+"&8, "));
            }
            sb.append(ChatColor.translateAlternateColorCodes('&',"&e"+pl[pl.length-1]));
            Bukkit.getServer().getConsoleSender().sendMessage(sb.toString());
            consoleMessage("&eIf you think this is not your fault report this error to the Developer.");
            consoleMessage("&cThe Plugin will now shutdown!");
            return false;
        }
        String holoVersion = Bukkit.getPluginManager().getPlugin("HolographicDisplays").getDescription().getVersion();
        consoleMessage("&eFound HolographicDisplays with Version&7: &b"+holoVersion);
        if(!holoVersion.startsWith("2.4"))
        {
            consoleMessage("&eBut you picked the wrong version&7: &6"+ Bukkit.getPluginManager().getPlugin("HolographicDisplays").getDescription().getVersion());
            consoleMessage("&cYou need at least the Version&7: &a2.4.0");
            consoleMessage("&cThe Plugin will now shutdown!");
            return false;
        }
        return true;
    }

    private void setupCommands(){
        this.getCommand("vproc").setExecutor(new AdminCommands());
    }

    private void setupEventListener(){
        Bukkit.getPluginManager().registerEvents(new Listeners(),this);
    }
}
