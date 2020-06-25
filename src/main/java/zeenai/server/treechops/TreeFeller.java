package zeenai.server.treechops;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import net.md_5.bungee.api.ChatColor;
import zeenai.server.PlayerConfig;

public class TreeFeller implements Listener, CommandExecutor {

    public static List<Block> RadiusBlocks(Location V, double Radius){
        List<Block> blocks = new ArrayList<Block>();
        // Calculate the blocks in this radius!
        Location cur = V.clone();
        double X = V.getX();
        double Y = V.getY();
        double Z=V.getZ();

        double FinalX = X+Radius;
        double FinalY = Y+Radius;
        double FinalZ = Z+Radius;

        for(double Yx = Y-Radius; Yx < FinalY; Yx+=1.0){
            for(double Xx = X-Radius; Xx < FinalX; Xx+=1.0){
                for(double Zx = Z-Radius; Zx < FinalZ; Zx+=1.0){
                    cur.setX(Xx);
                    cur.setY(Yx);
                    cur.setZ(Zx);

                    blocks.add(cur.getBlock());
                }
            }
        }

        return blocks;

    }

    
    public static List<Block> RadiusBlocksToMax(Location V, double Radius){
        List<Block> blocks = new ArrayList<Block>();
        // Calculate the blocks in this radius!
        Location cur = V.clone();
        double X = V.getX();
        double Y = V.getY();
        double Z=V.getZ();

        double FinalX = X+Radius;
        double FinalY = V.getWorld().getMaxHeight();
        double FinalZ = Z+Radius;

        for(double Yx = 0; Yx < FinalY; Yx+=1.0){
            for(double Xx = X-Radius; Xx < FinalX; Xx+=1.0){
                for(double Zx = Z-Radius; Zx < FinalZ; Zx+=1.0){
                    cur.setX(Xx);
                    cur.setY(Yx);
                    cur.setZ(Zx);

                    blocks.add(cur.getBlock());
                }
            }
        }

        return blocks;

    }
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBreakLog(BlockBreakEvent ev) {
        if(ev.getPlayer() == null)return;
        if(ev.getPlayer().getGameMode() != GameMode.SURVIVAL)return;

        FileConfiguration fc = PlayerConfig.GetConfig(ev.getPlayer());
        boolean enabled=true;
        if(fc.contains("autochop.state")) enabled=fc.getBoolean("autochop.state");


        if(!enabled)return;

        List<Material> TreeFellerTypes = new ArrayList<Material>();

        TreeFellerTypes.add(Material.OAK_LOG);
        TreeFellerTypes.add(Material.ACACIA_LOG);
        TreeFellerTypes.add(Material.ACACIA_LEAVES);
        TreeFellerTypes.add(Material.OAK_LEAVES);
        TreeFellerTypes.add(Material.DARK_OAK_LOG);
        TreeFellerTypes.add(Material.DARK_OAK_LEAVES);
        TreeFellerTypes.add(Material.BIRCH_LOG);
        TreeFellerTypes.add(Material.BIRCH_LEAVES);
        TreeFellerTypes.add(Material.JUNGLE_LOG);
        TreeFellerTypes.add(Material.JUNGLE_LEAVES);
        TreeFellerTypes.add(Material.SPRUCE_LOG);
        TreeFellerTypes.add(Material.SPRUCE_LEAVES);
        
        TreeFellerTypes.add(Material.STRIPPED_OAK_LOG);
        TreeFellerTypes.add(Material.STRIPPED_ACACIA_LOG);
        TreeFellerTypes.add(Material.STRIPPED_DARK_OAK_LOG);
        TreeFellerTypes.add(Material.STRIPPED_BIRCH_LOG);
        TreeFellerTypes.add(Material.STRIPPED_JUNGLE_LOG);
        TreeFellerTypes.add(Material.STRIPPED_SPRUCE_LOG);

        Map<Location, RestoreBlock> undoBuff = new HashMap<Location, RestoreBlock>();
        List<RestoreBlock> undoBuffer = new ArrayList<RestoreBlock>();


        if (ev.getBlock().getType().name().endsWith("LOG")) {
            // This is wood, break block, but then break any wood above it
            int triesY = 0; // This can only go up

            int maxTries = 5; // 5 block radius from last wood. If we dont find wood within 5 blocks, then we
                              // cancel operation!
            Location TreeLastCenter = ev.getBlock().getLocation();
            while (triesY != maxTries) {
                // search the X radius
                Location curLoc = TreeLastCenter.clone();
                
                List<Block> curRadius = TreeFeller.RadiusBlocks(curLoc, 2);
                while(curRadius.size()!=0){
                    Block b = curRadius.get(0);

                    RestoreBlock rb = new RestoreBlock();
                    rb.blkState = b.getState();
                    rb.loc = b.getLocation();
                    rb.world = b.getWorld().getName();
                    rb.biome = b.getBiome();
                    rb.mat = b.getType();

                    if(!undoBuff.containsKey(b.getLocation())) undoBuff.put(b.getLocation(), rb);
                    

                    curRadius.remove(0);

                    if(TreeFellerTypes.contains(b.getType())){
                        triesY=0;
                        // Drop the block
                        
                        b.breakNaturally();
                        
                        // get additional blocks from this position
                        for(Block XB : TreeFeller.RadiusBlocks(b.getLocation(), 2)){
                            curRadius.add(XB);
                        }
                    }
                }
                
                // now move up Y, check block, and perform same logic
                curLoc = TreeLastCenter.clone();
                curLoc.setY(curLoc.getY() + 1.0);
                Block B = curLoc.getBlock();
                if(TreeFellerTypes.contains(B.getType())){
                    
                    RestoreBlock rb = new RestoreBlock();
                    rb.blkState = B.getState();
                    rb.loc = B.getLocation();
                    rb.world = B.getWorld().getName();
                    rb.biome = B.getBiome();
                    rb.mat = B.getType();
                    if(!undoBuff.containsKey(B.getLocation()))undoBuff.put(B.getLocation(), rb);

                    B.breakNaturally();
                    triesY = 0;
                    TreeLastCenter = curLoc.clone();
                } else {
                    triesY++;
                    TreeLastCenter = curLoc.clone();
                }
            }
            ev.getPlayer().sendMessage("Tree has been chopped down!");

            Block orig = ev.getBlock();
            if(orig.getType() == Material.OAK_LOG){
                orig.setType(Material.OAK_SAPLING, false);
            }
            if(orig.getType() == Material.DARK_OAK_LOG){
                orig.setType(Material.DARK_OAK_SAPLING, false);
            }
            if(orig.getType() == Material.SPRUCE_LOG){
                orig.setType(Material.SPRUCE_SAPLING, false);
            }
            if(orig.getType() == Material.BIRCH_LOG){
                orig.setType(Material.BIRCH_SAPLING, false);
            }
            if(orig.getType() == Material.JUNGLE_LOG){
                orig.setType(Material.JUNGLE_SAPLING, false);
            }
            if(orig.getType() == Material.ACACIA_LOG){
                orig.setType(Material.ACACIA_SAPLING, false);
            }

            for (Map.Entry<Location, RestoreBlock> entry : undoBuff.entrySet()) {
                undoBuffer.add(entry.getValue());
            }
            undoBuff.clear();
            fc.set("autochop.undoBuffer", undoBuffer);

            ev.getPlayer().sendMessage("SAVED: "+undoBuffer.size()+" blocks to undo buffer");
            PlayerConfig.SaveConfig(ev.getPlayer());
        }
    }

    @Override
    public boolean onCommand( CommandSender sender,  Command command,  String label,
             String[] args)
    {
        FileConfiguration fc = PlayerConfig.GetConfig((Player)sender);
        if(fc.contains("autochop.state"))
            fc.set("autochop.state", !fc.getBoolean("autochop.state"));
        else
            fc.set("autochop.state", false);
        
        PlayerConfig.SaveConfig((Player)sender);

        sender.sendMessage("["+ChatColor.DARK_GREEN+"zAutoChop"+ChatColor.WHITE+"] "+ChatColor.GREEN+"Auto Chop has been set to "+ChatColor.DARK_PURPLE+fc.getBoolean("autochop.state"));

        return true;
    }


    @EventHandler
    public void tellState(PlayerJoinEvent ev){
        ev.getPlayer().sendMessage(ChatColor.WHITE+"["+ChatColor.DARK_GREEN+"zAutoChop"+ChatColor.WHITE+"] You current autochop preference is: "+PlayerConfig.GetConfig(ev.getPlayer()).getBoolean("autochop.state"));
    }
}