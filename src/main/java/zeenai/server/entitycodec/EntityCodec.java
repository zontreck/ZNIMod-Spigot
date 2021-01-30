package zeenai.server.entitycodec;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public interface EntityCodec
{
    EntityType[] getApplicableEntities();

    String serialize(Entity state);

    void deserialize(Entity state, String conf);
    String getID();

    String toString(String conf);
}