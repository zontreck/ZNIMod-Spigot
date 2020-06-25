package zeenai.server.schematics;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import zeenai.server.NullConfig;
import zeenai.server.schematics.writer.*;

public class POS2 implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender arg0, @NotNull Command arg1, @NotNull String arg2,
            @NotNull String[] arg3) {
        // Position 1 set
        Player p = (Player)arg0;
        Block X = p.getLocation().getBlock().getRelative(BlockFace.DOWN);
        Vector3 position = new Vector3(X.getLocation().getX(), X.getLocation().getY()+1.0, X.getLocation().getZ());
        NullConfig.GetConfig(p).set("Pos2",position);
        p.sendMessage(ChatColor.AQUA+"Position 2 set ("+position.ToString()+")");

        return true;
    }
    
}