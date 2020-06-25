package zeenai.server.antigrief;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class StartPhase2 implements CommandExecutor {

    @Override
    public boolean onCommand( CommandSender sender,  Command command,  String label,
             String[] args)
    {
        // Immediately cause phase 2 to fire
        Healer.GetInstance().LastBlock=0;
        Healer.GetInstance().Pass=2;
        
        return true;
    }
    
}