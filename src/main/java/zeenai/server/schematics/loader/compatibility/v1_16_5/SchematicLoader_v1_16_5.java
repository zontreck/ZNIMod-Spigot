package zeenai.server.schematics.loader.compatibility.v1_16_5;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import zeenai.server.Main;
import zeenai.server.NullConfig;
import zeenai.server.antigrief.Healer;
import zeenai.server.entitycodec.EntityCodecs;
import zeenai.server.entitycodec.RestoreEntity;
import zeenai.server.schematics.loader.compatibility.SchematicLoader;
import zeenai.server.schematics.writer.Vector3;
import zeenai.server.treechops.RestoreBlock;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SchematicLoader_v1_16_5 implements SchematicLoader {

    private CommandSender CurrentSender;
    private Map<Location, RestoreBlock> BlockList = new HashMap<Location, RestoreBlock>();
    private Map<Location, RestoreBlock> OriginalBlocks = new HashMap<Location, RestoreBlock>();
    private String WorldName;
    private boolean RemoveSchematic;

    private boolean loadStates=false;
    private boolean repairMode=false;
    private boolean air = false;

    @Override
    public void SetUndo(boolean undo){
        RemoveSchematic=undo;
    }


    public static void Requeue(Vector3 posx, RestoreEntity re, World _W) {
        new BukkitRunnable(){

            @Override
            public void run() {
                // 
                if(Main.GetMainInstance().forceQueue.size()>0){
                    Requeue(posx, re, _W);
                    return;
                }
                Vector3 relative = posx.Add(re.position);
                EntityType et = EntityType.valueOf(re.entityType);
                
                if(EntityCodecs.hasCodec(et)){
                    // Check for existing entity at this Vector3 position
                    Entity[] l_w_e = relative.GetBukkitLocation(_W).getChunk().getEntities();
                    for (Entity e_w_eEntity : l_w_e) {
                        if(new Vector3(e_w_eEntity.getLocation()).Same(relative)){
                            // Delete the existing entity from the world. 
                            // Only delete if it is not the player
                            if(e_w_eEntity.getType() != EntityType.PLAYER){

                                e_w_eEntity.remove();
                            }
                        }
                    }
                    Vector3 noprecision = relative.Clone();
                    noprecision.LosePrecision();
                    //actualEntityLocations.add(noprecision);
                    Entity e_e = _W.spawnEntity(relative.GetBukkitLocation(_W), et);
                    e_e.setGravity(false);
                    EntityCodecs.deserialize(e_e, re.serializedEntity);
                }
            }
            
        }.runTaskLater(Main.GetMainInstance(), 1000);
    }

    @Override
    public void LoadSchematic(File f) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.GetMainInstance(), new Runnable(){
        
            @Override
            public void run() {
                Main.GetMainInstance().getServer().broadcastMessage("Loading Schem3 with options: air:"+air+"; repair:"+repairMode+"; states:"+loadStates);
                int Seq=0;
                Vector3 PlayerPosition = null;
                List<Vector3> actualEntityLocations = new ArrayList<Vector3>();
                // We should also store a list of every entity found. We can then check positions against the restore list.
                Map<Vector3, Entity> allEntities = new HashMap<Vector3, Entity>();
                while(true){
                    File X = new File(f+"."+Seq+".schem3");
                    Main.GetMainInstance().getServer().broadcastMessage("Now loading: "+X.getName());
                    if(X.exists()){
                        // continue;
                    }else{
                        break;
                    }
                    FileConfiguration fc=null;
                    try{
                        fc = YamlConfiguration.loadConfiguration(X);
                    } catch(Exception e){
                        Main.GetMainInstance().getLogger().info("ERROR: Could not deserialize Yaml configuration!\n\n"+e.getMessage()+"\n\n");
                        e.printStackTrace();
                        CurrentSender.sendMessage("Error while deserializing");
                        return;
                    }
                    List<RestoreBlock> lRB = (List<RestoreBlock>)fc.getList("schematic.blocks");


                    Main.GetMainInstance().getServer().broadcastMessage("Loaded: "+X.getName());
                    //fc=null;
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
                    World _W = Bukkit.getWorld(WorldName);
                    if(fc.contains("schematic.entity")){
                        List<RestoreEntity> reLst  =  (List<RestoreEntity>) fc.getList("schematic.entity");
                        Player p = (Player)CurrentSender;
                        final Vector3 posx = PlayerPosition.Clone();
                        for(RestoreEntity re : reLst){
                            SchematicLoader_v1_16_5.Requeue(posx, re, _W);
                            
                        }
                    }
                    Main.GetMainInstance().getServer().broadcastMessage(ChatColor.AQUA+"Stand by.. Importing schem3");
                    for (RestoreBlock restoreBlock : lRB) {
                        if(restoreBlock.mat == Material.AIR && !air) {
                            //Main.GetMainInstance().getLogger().info("AIR RULE: Skipping Air");
                            continue;
                        }
                        Block currentBlock = restoreBlock.loc.GetBukkitLocation(_W).getBlock();
                        if(repairMode && currentBlock.getType() == restoreBlock.mat){
                            if(!restoreBlock.HasState()){
                                if(!air)continue;
                                else{
                                    if(restoreBlock.mat != Material.AIR){
                                        continue;
                                    }
                                }
                            }
                        }

                        if(currentBlock.getType().name().equals(restoreBlock.mat.name()) &&!RemoveSchematic && !restoreBlock.HasState()){
                            //Main.GetMainInstance().getLogger().info("Block already the same, skipping");
                            continue;
                        }
                        
                        Vector3 relative = new Vector3(restoreBlock.loc.getX(), restoreBlock.loc.getY(), restoreBlock.loc.getZ());
                        Vector3 absolute = relative.Add(PlayerPosition);
                        absolute.worldName = _W.getName();
                        restoreBlock.loc = absolute;
                        if(restoreBlock.mat.name().equalsIgnoreCase("air")){
                            restoreBlock.mat=Material.AIR;
                            restoreBlock.blkState=null;
                        }
                        if(!loadStates){
                            restoreBlock.blkState=null;
                        }
                        // Begin process of bringing in the schematic
                        if(RemoveSchematic) restoreBlock.mat = Material.AIR;
                        // Now, add the blocks to the appropriate lists
                        Entity[] le = currentBlock.getLocation().getChunk().getEntities();
                        for (Entity entity : le) {
                            Vector3 block = new Vector3(currentBlock.getLocation());
                            Vector3 ent = new Vector3(entity.getLocation());
                            if(block.Same(ent)){
                                allEntities.put(ent, entity);
                            }
                        }
    
                        if(!h.Queues.containsKey(restoreBlock.loc)) h.Queues.put(restoreBlock.loc, restoreBlock);
                    }
                    lRB=null;
                    fc=null;
                    Main.GetMainInstance().getServer().broadcastMessage(ChatColor.GREEN+"Schem3 added successfully to Heal Queue");
                    Seq++;
                }
                Main.GetMainInstance().getServer().broadcastMessage("Done requesting import");
                Main.GetMainInstance().getServer().broadcastMessage("Scan of entities now in progress");

                for(Entry<Vector3, Entity> entry : allEntities.entrySet()){
                    boolean isActual=false;
                    boolean duplicate=false;
                    // duplicate will be set if there are two in the same location. AKA isActual being set when it should not be.
                    for(Vector3 vec : actualEntityLocations){
                        if(vec.Same(entry.getKey())){
                            if(isActual)duplicate=true;

                            isActual=true;
                        }
                    }
                    if(entry.getValue().getType() == EntityType.PLAYER)continue;
                    if(duplicate)entry.getValue().remove();
                    if(!isActual)entry.getValue().remove();
                }

                Main.GetMainInstance().getServer().broadcastMessage("Entity Scan has Completed.");
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


    @Override
    public void SetLoadStates(boolean useStates) {
        this.loadStates=useStates;
        
    }


    @Override
    public void SetRepairMode(boolean repair) {
        this.repairMode=repair;
        
    }


    @Override
    public void SetIncludeAir(boolean air) {
        this.air=air;
    }
    
}