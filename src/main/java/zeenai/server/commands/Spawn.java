package zeenai.server.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import zeenai.server.Main;

public class Spawn implements CommandExecutor {

    @Override
    public boolean onCommand( CommandSender sender,  Command command,  String label,
             String[] args)

    {
        if(args.length!=0)return false;


        // check the configuration to ensure that spawn has been set
        if(Main.GetMainInstance().getConfig().getBoolean("spawn.IsSet")){
            // send player to spawn!

            Player p = (Player)sender;
            FileConfiguration fc = Main.GetMainInstance().getConfig();
            Location L = fc.getLocation("spawn.position");
            
            p.teleport(L);
            Bukkit.broadcastMessage(p.getName()+" has respawned!");

            
            
        }else{
            sender.sendMessage("You must first set a spawn using the ZNIMod plugin!");
        }

        return true;
    }
    
}