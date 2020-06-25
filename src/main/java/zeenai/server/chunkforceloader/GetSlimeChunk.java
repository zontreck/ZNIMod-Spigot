package zeenai.server.chunkforceloader;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class GetSlimeChunk implements CommandExecutor {

    @Override
    public boolean onCommand( CommandSender sender,  Command command,  String label,
             String[] args)
    {
        Player p = (Player)sender;
        Location L = p.getLocation();
        if(L.getBlock().getChunk().isSlimeChunk())
            p.sendMessage(ChatColor.GREEN+"This is a slime chunk");
        else
            p.sendMessage(ChatColor.RED+"This is not a slime chunk");

        return true;
    }
    
}