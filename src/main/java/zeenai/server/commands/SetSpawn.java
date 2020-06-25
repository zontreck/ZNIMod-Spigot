package zeenai.server.commands;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import zeenai.server.Main;


public class SetSpawn implements CommandExecutor {

    @Override
    public boolean onCommand( CommandSender sender,  Command command,  String label,
             String[] args)
    {
        if(args.length != 0)return false;

        Player p = (Player)sender;
        if(!p.hasPermission("znimod.opCommands")){
            // DENY
            sender.sendMessage(ChatColor.RED+"You lack the required permissions");
            return false;
        }
        
        // okay continue
        World W = Bukkit.getWorld(p.getWorld().getName());
        W.setSpawnLocation(p.getLocation());
        W.save();

        FileConfiguration fc = Main.GetMainInstance().getConfig();
        fc.set("spawn.position", p.getLocation());
        fc.set("spawn.IsSet",true);

        Main.GetMainInstance().saveConfig();

        sender.sendMessage("Success");

        return true;
    }
    
}