package zeenai.server.blockcodec;

import org.bukkit.Material;
import org.bukkit.block.BlockState;

public interface BlockStateCodec
{
    Material[] getApplicableMaterials();

    String serialize(BlockState state);

    void deserialize(BlockState state, String conf);
    String getID();

    String toString(String conf);
}