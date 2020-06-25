package zeenai.server.schematics.loader.compatibility;

import java.io.File;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import zeenai.server.treechops.RestoreBlock;

public interface SchematicLoader{

    //List<RestoreBlock> BlockList = new ArrayList<RestoreBlock>();

    public void SetPlayer(CommandSender sender);
    public void LoadSchematic(File f);
    public int GetBlockCount();
    public List<RestoreBlock> GetBlocks();
    public void SetUndo(boolean undo);
}