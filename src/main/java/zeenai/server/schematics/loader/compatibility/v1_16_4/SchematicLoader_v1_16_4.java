package zeenai.server.schematics.loader.compatibility.v1_16_4;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import zeenai.server.Main;
import zeenai.server.NullConfig;
import zeenai.server.antigrief.Healer;
import zeenai.server.blockcodec.BlockStateCodecs;
import zeenai.server.schematics.loader.compatibility.SchematicLoader;
import zeenai.server.schematics.writer.Vector3;
import zeenai.server.treechops.RestoreBlock;

public class SchematicLoader_v1_16_4 implements SchematicLoader {

    private CommandSender CurrentSender;
    private Map<Location, RestoreBlock> BlockList = new HashMap<Location, RestoreBlock>();
    private Map<Location, RestoreBlock> OriginalBlocks = new HashMap<Location, RestoreBlock>();
    private String WorldName;
    private boolean RemoveSchematic;

    @Override
    public void SetUndo(boolean undo){
        RemoveSchematic=undo;
    }


    @Override
    public void LoadSchematic(File f) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.GetMainInstance(), new Runnable(){
        
            @Override
            public void run() {
                int Seq=0;
                Vector3 PlayerPosition = null;
                while(true){
                    File X = new File(f+"."+Seq+".schem3");
                    if(X.exists()){
                        // continue;
                    }else{
                        break;
                    }
                    FileConfiguration fc = YamlConfiguration.loadConfiguration(X);

                    List<RestoreBlock> lRB = (List<RestoreBlock>)fc.getList("schematic.blocks");
                    fc=null;
                    Healer h = Healer.GetInstance();
                    if(Seq==0){
                        if(NullConfig.GetTempConfig(CurrentSender.getName()).get("Origin")==null){
                            if(CurrentSender instanceof Player){
                                Player CurrentPlayer = (Player)CurrentSender;
                                PlayerPosition = new Vector3(CurrentPlayer.getLocation().getX(), CurrentPlayer.getLocation().getY(), CurrentPlayer.getLocation().getZ());
                                WorldName = CurrentPlayer.getLocation().getWorld().getName();
                            }else{
                                CurrentSender.sendMessage("You must first set origin");
                                return;
                            }
                            // 
                        }else{
                            PlayerPosition = (Vector3)NullConfig.GetTempConfig(CurrentSender.getName()).get("Origin");
                            WorldName = (String) NullConfig.GetTempConfig(CurrentSender.getName()).get("World");
                        }
                    }
                    //CurrentPlayer.sendMessage(ChatColor.AQUA+"Stand by.. Importing schem3");
                    World _W = Bukkit.getWorld(WorldName);
                    for (RestoreBlock restoreBlock : lRB) {
                        if(restoreBlock.mat==Material.AIR && NullConfig.GetTempConfig(CurrentSender.getName()).getBoolean("IncludeAir")==false && NullConfig.GetTempConfig(CurrentSender.getName()).getBoolean("SetToAir")==false) continue;
                        Block currentBlock = restoreBlock.loc.getBlock();
                        if(currentBlock.getType().name() == restoreBlock.mat.name() &&!RemoveSchematic && !restoreBlock.HasState())continue;
                        
                        Vector3 relative = new Vector3(restoreBlock.loc.getX(), restoreBlock.loc.getY(), restoreBlock.loc.getZ());
                        Vector3 absolute = relative.Add(PlayerPosition);
                        restoreBlock.loc = new Location(_W, absolute.getX(), absolute.getY(), absolute.getZ());
                        // Begin process of bringing in the schematic
                        if(RemoveSchematic) restoreBlock.mat = Material.AIR;
                        // Now, add the blocks to the appropriate lists
    
                        if(!h.Queues.containsKey(restoreBlock.loc)) h.Queues.put(restoreBlock.loc, restoreBlock);
                    }
                    lRB=null;
                    //CurrentPlayer.sendMessage(ChatColor.GREEN+"Schem3 added successfully to Heal Queue");
                    Seq++;
                }
                CurrentSender.sendMessage("Done requesting import");
            }
        });
    }

    @Override
    public int GetBlockCount() {
        return BlockList.size();
    }

    @Override
    public List<RestoreBlock> GetBlocks() {
        return null;
    }

    @Override
    public void SetPlayer(CommandSender sender) {
        CurrentSender = sender;
    }
    
}