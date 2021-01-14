package zeenai.server.schematics.writer.compatibility.v1_16_r3;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import zeenai.server.schematics.writer.SchematicWriter;
import zeenai.server.schematics.writer.Vector3;
import zeenai.server.treechops.RestoreBlock;
import zeenai.server.*;
import zeenai.server.blockcodec.BlockStateCodecs;

public class SchematicWriter_v1_16_r3 implements SchematicWriter {

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

            List<Vector3> cubed = pos1.Cube(pos2);
            List<RestoreBlock> schem = new ArrayList<RestoreBlock>();
            CurrentPlayer.sendMessage(ChatColor.AQUA+"Processing information for "+cubed.size()+" blocks");
            Main.GetMainInstance().getLogger().info("Processing information for "+cubed.size()+" blocks");

            int CurBlock = 0;
            int Seq=0;
            while(cubed.size()!=0){
                Vector3 vector3 = cubed.get(0);
                RestoreBlock rb = new RestoreBlock();
                Location L = vector3.GetBukkitLocation(CurrentPlayer.getWorld());
                Block b = L.getBlock();
                if(b.getType()==Material.AIR){

                    // Grab the properties IF the null config has a specific flag
                    if(fc.getBoolean("IncludeAir")){
                        rb.biome=null;
                        rb.blkState=null;
                        rb.loc=L;
                        rb.mat=Material.AIR;
                        rb.world = b.getWorld().getName();
                        schem.add(rb);
                    }

                    cubed.remove(0);
                    vector3.Destroy();
                    continue;
                }

                CurBlock++;
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
                
                schem.add(rb);

                cubed.remove(vector3);

                vector3.Destroy();
                vector3=null;

                if(CurBlock >= 1000){

                    CurrentPlayer.sendMessage(ChatColor.DARK_GREEN+"In Progress ["+cubed.size()+" remaining]");
                    Main.GetMainInstance().getLogger().info("In Progress ["+cubed.size()+" remaining : "+schematicName+".schem3]");
                    CurBlock=0;
                    FileConfiguration schemSeq = new YamlConfiguration();
                    schemSeq.set("schematic.blocks",schem);
                    schemSeq.save(new File(F+"."+Seq+".schem3"));
                    schem.clear();
                    Seq++;
                }

                Main.GetMainInstance().getLogger().info("On block "+CurBlock+" of part "+Seq);
            }

            if(schem.size()>0){

                CurrentPlayer.sendMessage(ChatColor.DARK_GREEN+"In Progress ["+cubed.size()+" remaining]");
                Main.GetMainInstance().getLogger().info("In Progress ["+cubed.size()+" remaining : "+schematicName+".schem3]");
                FileConfiguration schemSeq = new YamlConfiguration();
                schemSeq.set("schematic.blocks",schem);
                schemSeq.save(new File(F+"."+Seq+".schem3"));
                schem.clear();
            }
            CurrentPlayer.sendMessage(ChatColor.GREEN+"Processing completed!");
            schem=null;
            
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