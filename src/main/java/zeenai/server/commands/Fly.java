package zeenai.server.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import zeenai.server.Main;
import zeenai.server.PlayerConfig;

public class Fly implements CommandExecutor {

    @Override
    public boolean onCommand( CommandSender sender,  Command command,  String label,
             String[] args)
    {
        Player p = (Player)sender;
        if(!p.hasPermission("znimod.flight")) {
            p.sendMessage(ChatColor.RED+"You require VIP Perks");
            return true;
        }
        p.setAllowFlight(!p.getAllowFlight());
        p.sendMessage(ChatColor.GREEN+"Success!");
        
        PlayerConfig.GetConfig(p).set("flight", p.getAllowFlight());
        PlayerConfig.SaveConfig(p);
        return true;
    }
    
}