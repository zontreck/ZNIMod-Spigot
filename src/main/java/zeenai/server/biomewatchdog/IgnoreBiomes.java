package zeenai.server.biomewatchdog;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import zeenai.server.PlayerConfig;

public class IgnoreBiomes implements CommandExecutor {

    @Override
    public boolean onCommand( CommandSender sender,  Command command,  String label,
             String[] args)
    {

        Player p = (Player)sender;

        // Set player preference
        PlayerConfig.GetConfig(p).set("ignorebiomes",true);
        PlayerConfig.SaveConfig(p);
        p.sendMessage("["+ChatColor.RED+"BIOME WATCHER"+ChatColor.WHITE+"] "+ChatColor.GREEN+" Will not alert to biome changes");
        return true;
    }
    
}