package zeenai.server.antigrief;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ClearBackup implements CommandExecutor {

    @Override
    public boolean onCommand( CommandSender sender,  Command command,  String label,
             String[] args)
    {
        if(sender.hasPermission("znimod.adminCommands")) Healer.GetInstance().backupMap.clear();

        return true;
    }
    
}