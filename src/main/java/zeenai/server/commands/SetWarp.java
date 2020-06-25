package zeenai.server.commands;

import java.time.Instant;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import zeenai.server.Main;

public class SetWarp implements CommandExecutor {

    @Override
    public boolean onCommand( CommandSender sender,  Command command,  String label,
             String[] args)
    {
        if(args.length != 1)return false;
        Player p = (Player)sender;

        // Create warp
        if(p.hasPermission("znimod.mkwarp")){

            ListWarps.GetConfig(p).set("warp."+args[0]+".loc", p.getLocation());
            ListWarps.GetConfig(p).set("warp."+args[0]+".owner", p.getName());
            ListWarps.GetConfig(p).set("warp."+args[0]+".creator", p.getName());
            ListWarps.GetConfig(p).set("warp."+args[0]+".created", Instant.now().getEpochSecond());
            ListWarps.GetConfig(p).set("warp."+args[0]+".perms", Warp.WarpFlags.OWNER_ONLY.name()); //<-- Default: Owner only; Will not show up on /warps
            ListWarps.SaveConfig(p);

            sender.sendMessage("Warp created!");
        } else {
            sender.sendMessage("You need the znimod.mkwarp permission!");

        }

        
        return true;
    }
    
}