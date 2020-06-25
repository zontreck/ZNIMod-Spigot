package zeenai.server.antigrief;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RestoreBackup implements CommandExecutor {

    @Override
    public boolean onCommand( CommandSender sender,  Command command,  String label,
             String[] args)
    {
        if(sender.hasPermission("znimod.adminCommands")){
            // Copy the backup Map to the Heal Map
            Healer.GetInstance().Queues = Healer.GetInstance().backupMap;

            return true;
        }else{
            sender.sendMessage("You do not have permission");
            return false;
        }
    }
    
}