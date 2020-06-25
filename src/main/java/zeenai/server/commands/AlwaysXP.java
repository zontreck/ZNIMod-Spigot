package zeenai.server.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import zeenai.server.Main;

public class AlwaysXP implements CommandExecutor, Listener {
    

    @Override
    public boolean onCommand( CommandSender sender,  Command command,  String label,
             String[] args)
    {
        Player p = (Player)sender;
        // Takes you back to your point of death
        FileConfiguration fc = Main.GetMainInstance().getConfig();
        
        

        if(fc.contains(p.getName()+".alwaysXP")){
            fc.set(p.getName()+".alwaysXP",null);
            sender.sendMessage("Disabled");
        }else {
            sender.sendMessage("Enabled");
            fc.set(p.getName()+".alwaysXP",true);
        }

        Main.GetMainInstance().saveConfig();


        return true;
    }

    
}