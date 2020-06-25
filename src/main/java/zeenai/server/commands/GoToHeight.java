package zeenai.server.commands;


import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GoToHeight implements CommandExecutor {

    @Override
    public boolean onCommand( CommandSender sender,  Command command,  String label,
             String[] args)
    {
        if(args.length!=1)return false;
        Player p = (Player)sender;

        if(!p.hasPermission("znimod.adminCommands")){
            sender.sendMessage("You do not have permission");
            return false;
        }

        Location L = p.getLocation();
        L.setY(Double.parseDouble(args[0]));
        
        p.teleport(L);

        return true;
    }
    
} 