package zeenai.server.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import zeenai.server.schematics.writer.Vector3;
import zeenai.server.treechops.TreeFeller;

public class ForceBlockUpdate implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender arg0, @NotNull Command arg1, @NotNull String arg2,
            @NotNull String[] arg3)
    {
        Vector3 v3 = new Vector3(Double.parseDouble(arg3[0]), Double.parseDouble(arg3[1]), Double.parseDouble(arg3[2]));
        
        List<Block> blocks = TreeFeller.RadiusBlocks(v3.GetBukkitLocation(Bukkit.getWorld(arg3[3])), Double.parseDouble(arg3[4]));
        for (Block block : blocks) {
            block.getState().update(true);
        }

        arg0.sendMessage("Updated blocks");

        return true;
    }

}