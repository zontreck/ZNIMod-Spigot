package zeenai.server.chunkforceloader;


import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GetChunkID implements CommandExecutor {

    @Override
    public boolean onCommand( CommandSender sender,  Command command,  String label,
             String[] args)
    {
        Player p = (Player)sender;
        Location L = p.getLocation();
        Chunk _chunk = L.getChunk();
        sender.sendMessage("X:"+_chunk.getX()+";Z:"+_chunk.getZ());

        return true;
    }
    
}