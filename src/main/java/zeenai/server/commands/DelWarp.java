package zeenai.server.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import zeenai.server.Main;

public class DelWarp implements CommandExecutor {

    @Override
    public boolean onCommand( CommandSender sender,  Command command,  String label,
             String[] args)
    {
        if(args.length != 1)return false;

        // Create warp
        if(sender.hasPermission("znimod.opCommands") || sender.hasPermission("znimod.adminCommands") || sender.getName().equals(ListWarps.GetConfig((Player)sender).getString("warp."+args[0]+".owner"))){

            ListWarps.GetConfig((Player)sender).set("warp."+args[0], null);
            ListWarps.SaveConfig((Player)sender);
    
            sender.sendMessage("Warp deleted");
        } else {
            sender.sendMessage("Warp deletion: Refused");
        }

        return true;
    }
    
}