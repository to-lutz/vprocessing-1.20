package de.verdox.vprocessing.model;

import de.verdox.vprocessing.VProcessing;
import de.verdox.vprocessing.dataconnection.PlayerSession;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ProcesserGUI extends GUI {
    private Processer processer;
    String processerID;
    public ProcesserGUI(Processer processer){
        super(45,processer.getName(),"Processer: "+processer.getProcesserID());
        this.processer = processer;
        this.processerID = processer.getProcesserID();
    }

    @Override
    public void setContent() {
        inv.setItem(22,GUI.process_Button());
    }

    @Override
    public void updateInventory() {
        new BukkitRunnable() {
            PlayerSession session;
            @Override
            public void run() {
                if(session==null&&inv.getViewers().size()>0){
                    Player p = (Player) inv.getViewers().get(0);
                    session = PlayerSession.getSession(p);
                }
                if(inv.getViewers().size()==0)
                    this.cancel();
                else {
                    if(session.hasPlayerTask(processer)){
                        ProcessTask task = session.getTask(processer);
                        if(task.isFinished()){
                            // Inventory updates to accept button!
                            System.out.println("Finished!");
                            inv.setItem(22,GUI.acceptButton());
                        }
                        else{
                            float difference = Math.abs((System.currentTimeMillis()-task.getTimestampStop())/1000);
                            float duration = (task.getTimestampStop()-task.getTimestampStarted())/1000;
                            int percentage = (int) ((1-(difference/duration))*100);
                            System.out.println(difference+" Sekunden noch!");
                            System.out.println(percentage+"%");
                            inv.setItem(22,GUI.idleButton());
                            // Inventory updates its layout!
                         }
                    }
                    else{
                        inv.setItem(22,GUI.process_Button());
                    }
                }
            }
        }.runTaskTimerAsynchronously(VProcessing.plugin,0L,20L);
    }
}
