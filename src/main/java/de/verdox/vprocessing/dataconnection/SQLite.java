package de.verdox.vprocessing.dataconnection;

import de.verdox.vprocessing.VProcessing;
import de.verdox.vprocessing.configuration.messages.SuccessMessage;
import de.verdox.vprocessing.model.ProcessTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.UUID;

public class SQLite extends DataConnectionImpl{

    private Plugin plugin;
    private File file;
    private Connection connection;

    public SQLite(Plugin plugin,String databaseName,String pluginDirectory){
        this.plugin = plugin;
        this.file = new File("plugins/"+pluginDirectory,databaseName+".db");
        if(!file.exists()){
            try {
                VProcessing.consoleMessage("&eCreated SQLite File at&7: &6"+file.getAbsolutePath());
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void connect() throws SQLException {
        if(connection!=null && !connection.isClosed()){
            return;
        }
        connection = DriverManager.getConnection("jdbc:sqlite:"+file.getAbsolutePath());
        initTables();
        VProcessing.consoleMessage("&bSQLite connection established!");
    }

    @Override
    public boolean disconnect() throws SQLException {
        if(connection==null ||connection.isClosed()){
            return false;
        }
        connection.close();
        return true;
    }

    @Override
    protected void initTables() throws SQLException {
        connection.createStatement().execute("CREATE TABLE IF NOT EXISTS process_task (\n"
        + "id integer PRIMARY KEY, \n"
        + "processerID text NOT NULL, \n"
        + "player_uuid text NOT NULL, \n"
        + "taskStarted integer, \n"
        + "taskEnd integer"
                +");");
        VProcessing.consoleMessage("&bSQLite Tables loaded loaded successfully!");
    }

    @Override
    public void createTask(ProcessTask task) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement
                ("INSERT INTO process_task (player_uuid,processerID,taskStarted,taskEnd) VALUES (?,?,?,?)");
        // Player_UUID
        preparedStatement.setString(1,task.getUuid().toString());
        // ProcesserID
        preparedStatement.setString(2,task.getProcesserID());
        // taskStarted
        preparedStatement.setLong(3,task.getTimestampStarted());
        // taskEnd
        preparedStatement.setLong(4,task.getTimestampStop());
        preparedStatement.executeUpdate();
    }

    @Override
    public void getTasksOfPlayer(Player p) {
        DataConnectionImpl.runAsync(() -> {
            try {
                ResultSet result = connection.createStatement().executeQuery("SELECT * FROM process_task WHERE player_uuid = '"+p.getUniqueId().toString()+"' ;");
                while(result.next()){
                    String processerID = result.getString("processerID");
                    Long taskStarted = result.getLong("taskStarted");
                    Long taskEnd = result.getLong("taskEnd");
                    if(!VProcessing.processConfiguration.exist(processerID))
                        return;
                    ProcessTask task = new ProcessTask(processerID,p.getUniqueId(),taskStarted,taskEnd);
                    PlayerSession.getSession(p).addTask(task);
                }
                Bukkit.getScheduler().runTask(VProcessing.plugin, () -> {
                    p.sendMessage(SuccessMessage.Successfully_loaded_files.getMessage());
                });
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, VProcessing.plugin);
    }

    @Override
    public void removeTask(UUID uuid, String processerID) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM process_task WHERE player_uuid = ? AND processerID = ? ;",Statement.RETURN_GENERATED_KEYS);
        // Player_UUID
        preparedStatement.setString(1,uuid.toString());
        // ProcesserID
        preparedStatement.setString(2,processerID);
        preparedStatement.executeUpdate();
    }
}
