package zeenai.server.commands;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import zeenai.server.Main;

public class BackToGrave implements CommandExecutor, Listener {
    public BackToGrave(){
        Main.GetMainInstance().getServer().getPluginManager().registerEvents(this, Main.GetMainInstance());
    }
    @Override
    public boolean onCommand( CommandSender sender,  Command command,  String label,
             String[] args)
    {
        Player p = (Player)sender;
        // Takes you back to your point of death
        FileConfiguration fc = Main.GetMainInstance().getConfig();
        if(fc.contains(p.getName()+".deathPos")){
            // Parse

            sender.sendMessage("Returning you to your point of death..");
            p.teleport(fc.getLocation(p.getName()+".deathPos"));

            fc.set(p.getName()+".deathPos",null);
            Main.GetMainInstance().saveConfig();


            return true;
        } else {
            sender.sendMessage("Sorry; No return point is set!");
            return true;
        }
    }


    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e){
        FileConfiguration fc = Main.GetMainInstance().getConfig();
        Random r = new Random();
        int dice = r.nextInt(20 +1 - 1)+1;

        if(fc.getBoolean(e.getEntity().getName()+".alwaysXP"))dice=20;

        if(dice>=13){
            Bukkit.broadcastMessage("1d20 = "+dice+"; "+e.getEntity().getName()+" can keep half their lost experience points! Congrats!");
            e.setNewExp(e.getDroppedExp()/2);
        } else {
            Bukkit.broadcastMessage("1d20 = "+dice+"; "+e.getEntity().getName()+" cannot keep their experience points");
            
        }
        fc.set(e.getEntity().getName()+".deathPos", e.getEntity().getLocation());
        Main.GetMainInstance().saveConfig();
    }
    
}