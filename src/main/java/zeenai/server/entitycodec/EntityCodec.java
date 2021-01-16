package zeenai.server.entitycodec;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;

public interface EntityCodec
{
    Material[] getApplicableMaterials();

    String serialize(Entity state);

    void deserialize(Entity state, String conf);
    String getID();

    String toString(String conf);
}