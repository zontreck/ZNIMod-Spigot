package zeenai.server.entitycodec;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.*;

import zeenai.server.Main;

public class EntityCodecs {
    public static Map<EntityType, EntityCodec> codecs = new HashMap<EntityType, EntityCodec>();
    public static void registerCodec(EntityCodec codec){
        for (EntityType entity : codec.getApplicableEntities()) {
            if(!codecs.containsKey(entity)){
                codecs.put(entity, codec);
            }
        }
    }
    static{
        registerCodec(new ArmorStandCodec());
    }


    public static boolean hasCodec(EntityType e){
        if(codecs.containsKey(e))return true;
        else return false;
    }

    public static String serialize(Entity state){
        EntityCodec codec = codecs.get(state.getType());

        if(codec != null){
            String serialized = codec.serialize(state);

            Main.GetMainInstance().getLogger().info("Serialized to : \n"+serialized);
            if(serialized != null && serialized.length()!=0){
                return serialized;
            }else {
                Main.GetMainInstance().getLogger().info("Serialized data not returned!");
            }
        }
        Main.GetMainInstance().getLogger().info("Could not find codec for "+state.getType());
        return null;
    }

    public static void deserialize(Entity block, String state){
        EntityCodec codec = codecs.get(block.getType());
        if(codec != null && state != null){
            codec.deserialize(block, state);
        }
    }
    
    public static String toString(EntityType mat, String state){
        EntityCodec codec = codecs.get(mat);
        if(codec != null){
            return codec.toString(state);
        }
        return null;
    }
}
