package zeenai.server.antigrief;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


public class ClearQueue implements CommandExecutor {

    @Override
    public boolean onCommand( CommandSender sender,  Command command,  String label,
             String[] args) 
    {
        if(!sender.hasPermission("znimod.adminCommands")){
            sender.sendMessage(ChatColor.RED+"Error: You lack permission");
            return false;
        }
        sender.sendMessage("Current queue has been cleared");

        Healer.GetInstance().Queues.clear();
        Healer.GetInstance().backupMap.clear();
        Healer.GetInstance().LastBlock=0;
        Healer.GetInstance().LastRestoreTime=0;
        Healer.GetInstance().Pass=0;

        

        return true;
    }
    
}