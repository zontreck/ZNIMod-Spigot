package zeenai.server.schematics;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import zeenai.server.NullConfig;
import zeenai.server.antigrief.Healer;
import zeenai.server.schematics.writer.*;
import zeenai.server.treechops.RestoreBlock;

public class FillAir implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender arg0, @NotNull Command arg1, @NotNull String arg2,
            @NotNull String[] arg3) {
        // Position 1 set
        Player p = (Player)arg0;
        
        // Start

        FileConfiguration fc = NullConfig.GetConfig(p);
        Vector3 pos1 = (Vector3)fc.get("Pos1");
        Vector3 pos2 = (Vector3)fc.get("Pos2");

        if(pos1 == null || pos2 == null){
            p.sendMessage(ChatColor.RED + "Error: positions are not set");
            return true;
        }
        Healer H = Healer.GetInstance();
        List<Vector3> positions = pos1.Cube(pos2);
        pos1.ForEachCubed(pos2, (val)->{
            RestoreBlock rb = new RestoreBlock();
            rb.mat = Material.AIR;
            Location L = new Location(p.getWorld(), val.getX(), val.getY(), val.getZ());
            rb.blkState = null;
            rb.loc = new Vector3(L);
            rb.world = p.getWorld().getName();
            rb.biome = L.getBlock().getBiome();

            RestoreBlock orig = new RestoreBlock();
            orig.biome = rb.biome;
            orig.blkState = L.getBlock().getState();
            orig.loc=new Vector3(L);
            orig.mat=L.getBlock().getType();
            orig.world=rb.world;

            if(!H.Queues.containsKey(rb.loc)){
                H.Queues.put(rb.loc,rb);
            }

            if(!H.backupMap.containsKey(orig.loc)){
                H.backupMap.put(orig.loc, orig);
            }
        });

        fc.set("Pos1",null);
        fc.set("Pos2",null);

        return true;
    }
    
}