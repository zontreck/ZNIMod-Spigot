package zeenai.server.antigrief;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import zeenai.server.schematics.writer.Vector3;
import zeenai.server.treechops.RestoreBlock;
import zeenai.server.treechops.TreeFeller;

public class ClearRadius implements CommandExecutor {

    @Override
    public boolean onCommand( CommandSender sender,  Command command,  String label,
             String[] args)
    {
        if(!sender.hasPermission("znimod.adminCommands")){
            sender.sendMessage("You do not have permission");
            return false;
        }
        double Radius = Double.parseDouble(args[0]);
        Player p = (Player)sender;

        List<Block> blocks = TreeFeller.RadiusBlocksToMax(p.getLocation(), Radius);

        for (Block block : blocks) {
            RestoreBlock rb = new RestoreBlock();
            rb.biome = block.getBiome();
            rb.blkState = block.getState();
            rb.loc=new Vector3(block.getLocation());
            rb.mat=block.getType();
            rb.world = block.getWorld().getName();

            if(!Healer.GetInstance().backupMap.containsKey(rb.loc))
                Healer.GetInstance().backupMap.put(rb.loc.GetBukkitLocation(block.getWorld()), rb);

            RestoreBlock rbAir = new RestoreBlock();
            rbAir.biome=block.getBiome();
            rbAir.loc = new Vector3(block.getLocation());
            rbAir.mat=Material.AIR;
            rbAir.world=block.getWorld().getName();
            if(!Healer.GetInstance().Queues.containsKey(rbAir.loc)) Healer.GetInstance().Queues.put(rbAir.loc.GetBukkitLocation(block.getWorld()), rbAir);
        }

        return true;
    }
    
}