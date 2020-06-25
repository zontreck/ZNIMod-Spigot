package zeenai.server.treechops;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import zeenai.server.PlayerConfig;

public class Undo implements CommandExecutor {

    @Override
    public boolean onCommand( CommandSender sender,  Command command, String label,
             String[] args)
    {

        // do undo
        List<RestoreBlock> undoBuffer = (List<RestoreBlock>)PlayerConfig.GetConfig((Player)sender).getList("autochop.undoBuffer");
        for (RestoreBlock block : undoBuffer) {
            World world = Bukkit.getWorld(block.world);    
            if(world==null){
                sender.sendMessage(ChatColor.RED+"error: unknown world");
                return true;
            }
            Location L = new Location(world, block.loc.getX(), block.loc.getY(), block.loc.getZ());
            Block actual = L.getBlock();
            if(actual == null){
                sender.sendMessage("ABORT: Block is null");
                return true;
            }
            Material oldType = actual.getType();
            actual.setType(block.mat, false);
            actual.setBiome( block.biome );
            
            block.ApplyState();
            
            if(oldType != block.mat){
                //sender.sendMessage("Error when undoing: "+ oldType.toString()+" isnt the same as "+block.mat.toString());
            }
        }
        
        PlayerConfig.GetConfig((Player)sender).set("autochop.undoBuffer",null);
        PlayerConfig.SaveConfig((Player)sender);
        sender.sendMessage("Done undoing");
        return true;
    }
    // this class executes a undo buffer
}