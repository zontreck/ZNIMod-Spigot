package zeenai.server.schematics.writer;

import java.io.File;

import org.bukkit.entity.Player;

public interface SchematicWriter
{
    public void SetCurrentPlayer(Player c);
    public void WriteToSchematic3(File F);
    public void SetSchematicName(String schem);
}
