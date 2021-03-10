package zeenai.server.blockcodec;

import java.util.EnumMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.BlockState;

import zeenai.server.Main;

public class BlockStateCodecs
{
    private static Map<Material, BlockStateCodec> codecs = new EnumMap<>(Material.class);

    public static void registerCodec(BlockStateCodec codec){
        Material[] materials = codec.getApplicableMaterials();
        for (Material material : materials) {
            if(codecs.containsKey(material)){
                Main.GetMainInstance().getLogger().info("WARN: Another codec has "+material.name()+" implemented. Codec ID is: "+codec.getID());
            }
            codecs.put(material,codec);
        }
    }

    static {
        registerCodec(new StateCodec_Sign());
        registerCodec(new StateCodec_Directional());
        registerCodec(new StateCodec_Orientable());
        registerCodec(new StateCodec_Stairs());
        registerCodec(new StateCodec_Slabs());
        registerCodec(new StateCodec_GlassPane());
        registerCodec(new StateCodec_Fence());
        registerCodec(new StateCodec_TrapDoor());
        registerCodec(new StateCodec_Rotatable());
        registerCodec(new StateCodec_Switch());
        registerCodec(new StateCodec_Door());
        registerCodec(new StateCodec_Wall());
        registerCodec(new StateCodec_Banner());
        registerCodec(new StateCodec_Container());
        registerCodec(new StateCodec_Levelled());
        registerCodec(new StateCodec_Gate());
        registerCodec(new StateCodec_Lantern());
    }

    public static boolean hasCodec(Material mat){
        return codecs.containsKey(mat);
    }

    public static String serialize(BlockState state){
        BlockStateCodec codec = codecs.get(state.getType());

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

    public static void deserialize(BlockState block, String state){
        BlockStateCodec codec = codecs.get(block.getType());
        if(codec != null && state != null){
            codec.deserialize(block, state);
        }
    }

    public static String toString(Material mat, String state){
        BlockStateCodec codec = codecs.get(mat);
        if(codec != null){
            return codec.toString(state);
        }
        return null;
    }
}