package zeenai.server.antigrief;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;

public class CheckQueue implements CommandExecutor {

    @Override
    public boolean onCommand( CommandSender sender,  Command command,  String label,
             String[] args) 
    {
        sender.sendMessage("Current queue has "+ChatColor.GOLD+Healer.GetInstance().Queues.size()+" blocks pending restore");
        sender.sendMessage("Last Block restored: "+ChatColor.GOLD+Healer.GetInstance().LastBlock);

        return true;
    }
    
}