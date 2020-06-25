package zeenai.server.schematics;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import zeenai.server.schematics.writer.*;

public class GetPos implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender arg0, @NotNull Command arg1, @NotNull String arg2,
            @NotNull String[] arg3) {
        // Position 1 set
        Player p = (Player)arg0;
        Vector3 position = new Vector3(p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ());
        p.sendMessage(ChatColor.AQUA+"Position is "+position.ToString());

        return true;
    }
    
}