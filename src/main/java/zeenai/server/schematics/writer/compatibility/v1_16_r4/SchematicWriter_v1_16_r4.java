package zeenai.server.schematics.writer.compatibility.v1_16_r4;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import zeenai.server.Main;
import zeenai.server.NullConfig;
import zeenai.server.blockcodec.BlockStateCodecs;
import zeenai.server.entitycodec.EntityCodecs;
import zeenai.server.entitycodec.RestoreEntity;
import zeenai.server.schematics.writer.SchematicWriter;
import zeenai.server.schematics.writer.Vector3;
import zeenai.server.treechops.RestoreBlock;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class SchematicWriter_v1_16_r4 implements SchematicWriter {

    public Player CurrentPlayer;
    public String schematicName;


    @Override
    public void SetCurrentPlayer(Player c) {
        CurrentPlayer=c;
    }

    @Override
    public void WriteToSchematic3(File F) {
        try {
            Random rnd = new Random();
            FileConfiguration fc = NullConfig.GetConfig(CurrentPlayer);

            Vector3 pos1 = (Vector3)fc.get("Pos1");
            Vector3 pos2 = (Vector3)fc.get("Pos2");
            Map<Vector3, Entity> uniqueEntityMap = new HashMap<Vector3, Entity>();
            pos2.worldName=CurrentPlayer.getWorld().getName();
            pos1.worldName=CurrentPlayer.getWorld().getName();
            //List<Vector3> cubed = pos1.Cube(pos2);
            //List<RestoreBlock> schem = new ArrayList<RestoreBlock>();
            

            FileConfiguration fxx = NullConfig.GetTempConfig("schematicWriter");
            fxx.set("schematicWriter.current",0);
            fxx.set("schematicWriter.schem", 0);
            fxx.set("schematicWriter.schematic",new ArrayList<RestoreBlock>());
            
            pos1.ForEachCubed(pos2, (val)->{
                Vector3 vector3 = val;
                FileConfiguration fx = NullConfig.GetTempConfig("schematicWriter");
                int xCurBlock = fx.getInt("schematicWriter.current");
                int xSeq = fx.getInt("schematicWriter.schem");
                List<RestoreBlock> schema = (List<RestoreBlock>)fx.getList("schematicWriter.schematic");


                RestoreBlock rb = new RestoreBlock();
                Location L = vector3.GetBukkitLocation(CurrentPlayer.getWorld());
                Block b = L.getBlock();
                if(!b.getChunk().isLoaded()){
                    b.getChunk().load();
                    while(!b.getChunk().isLoaded()){}
                }
                b.getWorld().playSound(b.getLocation(), Sound.ENTITY_ITEM_PICKUP,1F,rnd.nextFloat() * 2);
                b.getWorld().playEffect(b.getLocation(), Effect.MOBSPAWNER_FLAMES, 50);
                Entity[] ens = L.getChunk().getEntities();
                for (Entity entity : ens) {
                    boolean doesNotHaveEntry=true;
                    for (Entry<Vector3, Entity> entity2 : uniqueEntityMap.entrySet()) {
                        if(entity2.getKey().Same(new Vector3(entity.getLocation())))doesNotHaveEntry=false;
                        //else Main.GetMainInstance().getLogger().info("Vectors are not the same ("+entity2.getKey().ToString()+") / ("+new Vector3(entity.getLocation()).ToString()+")");
                    }
                    if(doesNotHaveEntry){
                        if(EntityCodecs.hasCodec(entity.getType()))
                            uniqueEntityMap.put(new Vector3(entity.getLocation()), entity);
                    }
                }


                
                if(b.getType()==Material.AIR){

                    // Grab the properties IF the null config has a specific flag
                    if(fc.getBoolean("IncludeAir")){
                        //xCurBlock++;
                        rb.biome = b.getBiome();
                        rb.blkState=null;
                
                        Vector3 relative = vector3.Sub(pos1);
                        rb.loc = relative.GetBukkitLocation(b.getWorld());
                        rb.mat=Material.AIR;
                        rb.world = b.getWorld().getName();
                        rb.relative=pos1;
                        schema.add(rb);
                        //fx.set("schematicWriter.current", xCurBlock);
                        fx.set("schematicWriter.schematic", schema);
                    }
                    return;
                }

                xCurBlock++;
                fx.set("schematicWriter.current", xCurBlock);
                rb.biome = b.getBiome();
                if(BlockStateCodecs.hasCodec(b.getType())){
                    
                    Main.GetMainInstance().getLogger().info("Codec is available for "+b.getType().name());
                    if(!Main.GetMainInstance().stateRequests.contains(L)) Main.GetMainInstance().stateRequests.add(L);
                    Main.GetMainInstance().getLogger().info("Snapshot Waiting for "+b.getType().name());
                    while(!Main.GetMainInstance().states.containsKey(L)){
                        // state is not yet requested from server
                    }
                    rb.blkState=Main.GetMainInstance().states.get(L);

                    Main.GetMainInstance().getLogger().info("Snapshot Saved for "+b.getType().name());
                    
                    try{
                        Main.GetMainInstance().stateRequests.remove(L);

                    }catch(Exception e){}
                    try{

                        Main.GetMainInstance().states.remove(L);
                    } catch(Exception e){}
                    
                }else{
                    rb.blkState=null;
                }

                
                Vector3 relative = vector3.Sub(pos1);
                rb.loc = relative.GetBukkitLocation(b.getWorld());
                rb.mat = b.getType();
                rb.world = b.getWorld().getName();
                rb.relative = pos1;
                
                schema.add(rb);
                fx.set("schematicWriter.schematic", schema);

                if(xCurBlock >= 1000){

                    xCurBlock=0;
                    fx.set("schematicWriter.current", 0);
                    FileConfiguration schemSeq = new YamlConfiguration();
                    schemSeq.set("schematic.blocks",schema);
                            
                    //EntityCodecs codecs = new EntityCodecs();
                    
                    try {
                        schemSeq.save(new File(F+"."+xSeq+".schem3"));
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    schema.clear();
                    fx.set("schematicWriter.schematic", schema);
                    xSeq++;
                    fx.set("schematicWriter.schem", xSeq);
                }

                Main.GetMainInstance().getLogger().info("On block "+xCurBlock+" of part "+xSeq);
            });
            int entityNumber=0;
            List<RestoreEntity> serializedEntity = new ArrayList<RestoreEntity>();

            List<Vector3> schem = (List<Vector3>)fxx.getList("schematicWriter.schematic");
            int Seq = fxx.getInt("schematicWriter.schem");
            for (Entry<Vector3, Entity> ent:uniqueEntityMap.entrySet()) {
                Main.GetMainInstance().getLogger().info("On Entity: "+ent.getValue().getType().name());
                
                // Entities can now be serialized
                // Serialize entities to {schematic.entity}
                if(EntityCodecs.hasCodec(ent.getValue().getType())){
                    Main.GetMainInstance().getLogger().info("Codec found!");

                    RestoreEntity re = new RestoreEntity();
                    re.entityType = ent.getValue().getType().name();
                    re.position = Vector3.LosslessVector3(ent.getValue().getLocation()).Sub(pos1);
                    re.serializedEntity = EntityCodecs.serialize(ent.getValue());
                    serializedEntity.add(re);
                    
                    entityNumber++;
                }else{
                    Main.GetMainInstance().getLogger().info("No codec could be found");
                }
            }

            if(!schem.isEmpty() ||!serializedEntity.isEmpty()){

                FileConfiguration schemSeq = new YamlConfiguration();
                if(!schem.isEmpty())
                    schemSeq.set("schematic.blocks",schem);
                if(!serializedEntity.isEmpty()) schemSeq.set("schematic.entity", serializedEntity);
                schemSeq.save(new File(F+"."+Seq+".schem3"));
                schem.clear();
            }
            CurrentPlayer.sendMessage(ChatColor.GREEN+"Processing completed!");
            schem=null;

            fxx.set("schematicWriter.current",0);
            fxx.set("schematicWriter.schem",0);
            fxx.set("schematicWriter.schematic",new ArrayList<RestoreBlock>());
            
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void SetSchematicName(String schem) {
        schematicName=schem;
    }
    
}