package zeenai.server.treechops;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import zeenai.server.Main;
import zeenai.server.PlayerConfig;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.util.collection.BlockMap;

public class ViewRadius implements CommandExecutor {

    @Override
    public boolean onCommand( CommandSender sender,  Command command,  String label,
             String[] args)
    {
        Player p  = (Player)sender;
        /*if(!Main.GetMainInstance().hasWorldEdit){
            sender.sendMessage(ChatColor.RED+"Error: World edit is not installed. This command is unavailable");
            return true;
        }*/
        if(p.hasPermission("znimod.adminCommands")){

            List<Block> radius = TreeFeller.RadiusBlocks(p.getLocation(), Integer.parseInt(args[0]));
            List<RestoreBlock> undoBuffer = new ArrayList<RestoreBlock>();
            
            //EditSession ES = Main.GetMainInstance()._worldEditor.createEditSession(p);
            
            
            for (Block b : radius) {
                
                RestoreBlock rb = new RestoreBlock();
                rb.blkState = b.getState();
                rb.loc = b.getLocation();
                rb.world = b.getWorld().getName();
                rb.biome = b.getBiome();
                rb.mat = b.getType();
                undoBuffer.add(rb);

                b.setType(Material.BEDROCK, false);
            }

            PlayerConfig.GetConfig(p).set("autochop.undoBuffer", undoBuffer);

            sender.sendMessage("Saved "+undoBuffer.size()+" blocks");
            PlayerConfig.SaveConfig(p);
    
            return true;
        } else{
            sender.sendMessage("You must be admin");
            return false;
        }
    }
    
}