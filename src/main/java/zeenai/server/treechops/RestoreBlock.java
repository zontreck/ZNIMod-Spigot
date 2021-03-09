package zeenai.server.treechops;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.NullArgumentException;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.yaml.snakeyaml.Yaml;

import zeenai.server.Main;
import zeenai.server.blockcodec.BlockStateCodecs;
import zeenai.server.schematics.writer.Vector3;


public class RestoreBlock implements ConfigurationSerializable, Cloneable {
    public Location loc;
    public BlockState blkState;
    public String world;
    public Biome biome;
    public Material mat;

    private String state;

    public Vector3 relative; // This is the pos1 value, so that we can calculate a precise position if serialization goes wrong with a relative position.

    public boolean HasState(){
        if(blkState!=null)return true;
        if(state!=null)return true;
        if(state=="")return false;

        return false;
    }
    
    public RestoreBlock() {
    }

    private RestoreBlock(Location l, String w, Biome b, Material m, String state) {
        loc = l;
        world = w;

        if(b == null) biome = null;
        else
            biome = b;

        if(m == null) mat= Material.AIR;
        else
            mat = m;

        if(state!=null){
            this.state=state;
        }
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();

        data.put("loc", loc);
        
        data.put("world", world);

        if(biome!= null)
            data.put("biome", biome.name());
        
        if(mat!=null)
            data.put("mat", mat.name());

        try{

            if(blkState!= null)
                data.put("blockState", BlockStateCodecs.serialize(blkState));
        }catch(Exception e){
            Main.GetMainInstance().getLogger().info("Warning: Block at location: "+new Vector3(loc).Add(relative).ToString()+" has failed export: "+e.getMessage());
            return null;
        }

        
        return data;
    }

    public static RestoreBlock deserialize(Map<String, Object> mp) {
        String blk = (String) mp.get("blockState");
        Biome _biome=null;
        if(mp.get("biome")!=null){
            _biome = Biome.valueOf((String) mp.get("biome"));
        }

        return new RestoreBlock((Location) mp.get("loc"), (String) mp.get("world"),
                _biome, Material.getMaterial((String) mp.get("mat")), blk);
    }

    public void ApplyState() {
        // This should be called after the block's type is set
        if(blkState==null){

            blkState = loc.getBlock().getState();

            BlockStateCodecs.deserialize(blkState, state);
        }
        
        blkState.update(true);
    }

    public RestoreBlock Clone() {
        try {
            return (RestoreBlock) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
        
}