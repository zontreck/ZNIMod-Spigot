package zeenai.server.antigrief;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import zeenai.server.Main;
import zeenai.server.schematics.writer.Vector3;
import zeenai.server.treechops.RestoreBlock;
import zeenai.server.treechops.TreeFeller;

public class BlockIgniteListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true )
    public void onBlockExplode(BlockExplodeEvent ev){
        FileConfiguration fc = AntiGrief.GetConfig();
        if(fc.getBoolean("antigrief.healExplode")){
            List<Block> blkList = ev.blockList();
            List<Block> fullList = new ArrayList<Block>();
            //Main.GetMainInstance().getLogger().info("[AntiGrief WorldName]: "+ev.getBlock().getLocation().getWorld().getName());
            if(!fc.getBoolean("antigrief.healNetherExplode")){
                if(ev.getBlock().getLocation().getWorld().getName().compareToIgnoreCase(fc.getString("antigrief.netherName"))==0){
                    return;
                }
            }
            //Main.GetMainInstance().getLogger().info("Entity Exploded!");
            for (Block block : blkList) {
                for (Block block2 : TreeFeller.RadiusBlocks(block.getLocation(), 3)) {
                    fullList.add(block2);
                }
            }
            
            for (Block b : fullList) {
                    
                RestoreBlock rb = new RestoreBlock();
                rb.biome = b.getBiome();
                rb.blkState = b.getState();
                rb.loc = new Vector3(b.getLocation());
                rb.mat = b.getType();
                rb.world = b.getWorld().getName();
                Healer.GetInstance().TNT=true;
                if(!Healer.GetInstance().Queues.containsKey(rb.loc)) Healer.GetInstance().Queues.put(rb.loc.GetBukkitLocation(Main.GetMainInstance().getServer().getWorld(rb.world)), rb);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true )
    public void onEntityExplode(EntityExplodeEvent ev){
        FileConfiguration fc = AntiGrief.GetConfig();
        if(fc.getBoolean("antigrief.healExplode")){
            //Main.GetMainInstance().getLogger().info("[AntiGrief WorldName]: "+ev.getLocation().getWorld().getName());
            if(!fc.getBoolean("antigrief.healNetherExplode")){
                if(ev.getLocation().getWorld().getName().compareToIgnoreCase(fc.getString("antigrief.netherName"))==0){
                    return;
                }
            }
            //Main.GetMainInstance().getLogger().info("Entity Exploded!");
            List<Block> blkList = ev.blockList();
            List<Block> fullList = new ArrayList<Block>();

            for (Block block : blkList) {
                for (Block block2 : TreeFeller.RadiusBlocks(block.getLocation(), 3)) {
                    fullList.add(block2);
                }
            }
            
            for (Block b : fullList) {
                    
                RestoreBlock rb = new RestoreBlock();
                rb.biome = b.getBiome();
                rb.blkState = b.getState();
                rb.loc = new Vector3(b.getLocation());
                rb.mat = b.getType();
                rb.world = b.getWorld().getName();
                Healer.GetInstance().TNT=true;
                if(!Healer.GetInstance().Queues.containsKey(rb.loc)) Healer.GetInstance().Queues.put(rb.loc.GetBukkitLocation(Main.GetMainInstance().getServer().getWorld(rb.world)), rb);
            }
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true )
    public void onEntityExplode(EntityBreakDoorEvent ev){
        FileConfiguration fc = AntiGrief.GetConfig();
        if(fc.getBoolean("antigrief.healExplode")){

            //Main.GetMainInstance().getLogger().info("[AntiGrief WorldName]: "+ev.getBlock().getLocation().getWorld().getName());
            if(!fc.getBoolean("antigrief.healNetherExplode")){
                if(ev.getBlock().getLocation().getWorld().getName().compareToIgnoreCase(fc.getString("antigrief.netherName"))==0){
                    return;
                }
            }
            //Main.GetMainInstance().getLogger().info("Entity Exploded!");
            List<Block> blkList = new ArrayList<Block>();
            blkList.add(ev.getBlock());
            List<Block> fullList = new ArrayList<Block>();

            for (Block block : blkList) {
                for (Block block2 : TreeFeller.RadiusBlocks(block.getLocation(), 3)) {
                    fullList.add(block2);
                }
            }
            
            for (Block b : fullList) {
                    
                RestoreBlock rb = new RestoreBlock();
                rb.biome = b.getBiome();
                rb.blkState = b.getState();
                rb.loc = new Vector3(b.getLocation());
                rb.mat = b.getType();
                rb.world = b.getWorld().getName();
                if(!Healer.GetInstance().Queues.containsKey(rb.loc)) Healer.GetInstance().Queues.put(rb.loc.GetBukkitLocation(Main.GetMainInstance().getServer().getWorld(rb.world)), rb);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onIgnite(BlockIgniteEvent ev){
        if(ev.getCause() == IgniteCause.LAVA || ev.getCause() == IgniteCause.SPREAD){
            if(AntiGrief.GetConfig().getBoolean("antigrief.healFire")) ev.setCancelled(true);
        }
    }

}