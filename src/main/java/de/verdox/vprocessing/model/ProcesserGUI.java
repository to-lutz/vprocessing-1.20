package de.verdox.vprocessing.model;

import de.verdox.vprocessing.VProcessing;
import de.verdox.vprocessing.dataconnection.PlayerSession;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ProcesserGUI extends GUI {
    private Processer processer;
    public static String identifier = "Processer: ";
    String processerID;
    public ProcesserGUI(Processer processer){
        super(45,processer.getName(),identifier+processer.getProcesserID());
        this.processer = processer;
        this.processerID = processer.getProcesserID();
    }

    @Override
    public void setContent() {
        inv.setItem(22,GUI.process_Button(processer));
        inv.setItem(10,GUI.borderBlack());
        inv.setItem(11,GUI.borderBlack());
        inv.setItem(12,GUI.borderBlack());
        inv.setItem(13,GUI.borderBlack());
        inv.setItem(14,GUI.borderBlack());
        inv.setItem(15,GUI.borderBlack());
        inv.setItem(16,GUI.borderBlack());
        inv.setItem(19,GUI.borderBlack());
        inv.setItem(25,GUI.borderBlack());
        inv.setItem(28,GUI.borderBlack());
        inv.setItem(29,GUI.borderBlack());
        inv.setItem(30,GUI.borderBlack());
        inv.setItem(31,GUI.borderBlack());
        inv.setItem(32,GUI.borderBlack());
        inv.setItem(33,GUI.borderBlack());
        inv.setItem(34,GUI.borderBlack());
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
                            //System.out.println("Finished!");
                            inv.setItem(20,GUI.acceptButton());
                            inv.setItem(21,GUI.acceptButton());
                            inv.setItem(22,GUI.acceptButton());
                            inv.setItem(23,GUI.acceptButton());
                            inv.setItem(24,GUI.acceptButton());

                            inv.setItem(9,GUI.acceptButton());
                            inv.setItem(18,GUI.acceptButton());
                            inv.setItem(27,GUI.acceptButton());

                            inv.setItem(17,GUI.acceptButton());
                            inv.setItem(26,GUI.acceptButton());
                            inv.setItem(35,GUI.acceptButton());

                            inv.setItem(13,GUI.borderBlack());
                            inv.setItem(31,GUI.borderBlack());
                        }
                        else{
                            float difference = Math.abs((System.currentTimeMillis()-task.getTimestampStop())/1000);
                            float duration = (task.getTimestampStop()-task.getTimestampStarted())/1000;
                            int percentage = (int) ((1-(difference/duration))*100);
                            //System.out.println(difference+" Sekunden noch!");
                            //System.out.println(percentage+"%");

                            inv.setItem(22,null);

                            inv.setItem(9,GUI.idleButton());
                            inv.setItem(18,GUI.idleButton());
                            inv.setItem(27,GUI.idleButton());

                            inv.setItem(17,GUI.idleButton());
                            inv.setItem(26,GUI.idleButton());
                            inv.setItem(35,GUI.idleButton());

                            // Inventory updates its layout!
                            if(percentage>=20)
                                inv.setItem(20,GUI.percentage(20));
                            if(percentage>=40)
                                inv.setItem(21,GUI.percentage(40));
                            if(percentage>=60)
                                inv.setItem(22,GUI.percentage(60));
                            if(percentage>=80)
                                inv.setItem(23,GUI.percentage(80));
                            if(percentage>=100)
                                inv.setItem(24,GUI.percentage(100));

                            inv.setItem(13,GUI.activeTaskButton(task));
                            inv.setItem(31,GUI.cancelButton());
                         }
                    }
                    else{
                        inv.setItem(22,GUI.process_Button(processer));

                        inv.setItem(9,GUI.process_Button(null));
                        inv.setItem(18,GUI.process_Button(null));
                        inv.setItem(27,GUI.process_Button(null));

                        inv.setItem(17,GUI.process_Button(null));
                        inv.setItem(26,GUI.process_Button(null));
                        inv.setItem(35,GUI.process_Button(null));

                        inv.setItem(20,null);
                        inv.setItem(21,null);
                        inv.setItem(23,null);
                        inv.setItem(24,null);

                        inv.setItem(13,GUI.borderBlack());
                        inv.setItem(31,GUI.borderBlack());
                    }
                }
            }
        }.runTaskTimerAsynchronously(VProcessing.plugin,0L,20L);
    }
}
