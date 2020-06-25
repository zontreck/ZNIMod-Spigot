package zeenai.server.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import zeenai.server.Main;

public class Warp implements CommandExecutor {

    public enum WarpFlags
    {
        OWNER_ONLY, 
        NAME_KNOWN,
        FULL
    }

    @Override
    public boolean onCommand( CommandSender sender,  Command command,  String label,
             String[] args)
    {
        if(args.length != 1)return false;
        Player p = (Player)sender;

        if(p.hasPermission("znimod.opCommands") || p.hasPermission("znimod.adminCommands") || p.getName().equals(ListWarps.GetConfig(p).getString("warp"+args[0]+".owner")) ||
                ListWarps.GetConfig(p).getString("warp."+args[0]+".perms") == Warp.WarpFlags.FULL.name() || ListWarps.GetConfig(p).getString("warp."+args[0]+".perms") == Warp.WarpFlags.NAME_KNOWN.name()){

            p.teleport(ListWarps.GetConfig(p).getLocation("warp."+args[0]+".loc"));
            sender.sendMessage("Warping!");
        } else {
            sender.sendMessage(ChatColor.RED+"You do not have permission");
        }

        return true;
    }
    
}