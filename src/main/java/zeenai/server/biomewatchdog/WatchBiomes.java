package zeenai.server.biomewatchdog;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import zeenai.server.PlayerConfig;

public class WatchBiomes implements CommandExecutor {

    @Override
    public boolean onCommand( CommandSender sender,  Command command,  String label,
             String[] args)
    {

        Player p = (Player)sender;

        // Set player preference
        PlayerConfig.GetConfig(p).set("ignorebiomes",false);
        PlayerConfig.SaveConfig(p);

        p.sendMessage("["+ChatColor.RED+"BIOME WATCHER"+ChatColor.WHITE+"] "+ChatColor.GREEN+" Will now alert to biome changes");
        return true;
    }
    
}