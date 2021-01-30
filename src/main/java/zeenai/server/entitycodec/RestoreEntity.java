package zeenai.server.entitycodec;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import zeenai.server.schematics.writer.Vector3;

public class RestoreEntity implements ConfigurationSerializable, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public String entityType;
    public Vector3 position;
    public String serializedEntity;

    public RestoreEntity() {
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> ret = new HashMap<String, Object>();
        ret.put("type", entityType);
        ret.put("position", position);
        ret.put("entity", serializedEntity);

        return ret;
    }

    public static RestoreEntity deserialize(Map<String, Object> mp){
        RestoreEntity re = new RestoreEntity();
        re.entityType=(String)mp.get("type");
        re.position = (Vector3)mp.get("position");
        re.serializedEntity=(String)mp.get("entity");
        return re;
        
    }
}
