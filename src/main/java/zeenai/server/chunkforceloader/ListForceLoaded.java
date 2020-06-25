package zeenai.server.chunkforceloader;

import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import zeenai.server.Main;

public class ListForceLoaded implements CommandExecutor {

    @Override
    public boolean onCommand( CommandSender sender,  Command command,  String label,
             String[] args)
    {
        if(!sender.hasPermission("znimod.adminCommands")){
            sender.sendMessage("You lack permissions");
            return false;
        }
        // start scan: config
        FileConfiguration fc = Main.GetMainInstance().getConfig();
        if(!fc.contains("flchunk")){
            sender.sendMessage("Nothing is currently force loaded");
            return true;
        }
        Set<String> worldChunks = fc.getConfigurationSection("flchunk").getKeys(false);
        for (String _world : worldChunks) {
            // loop through the x and z coords
            World actualWorld = null;
            List<World> worlds= Main.GetMainInstance().getServer().getWorlds();
            for (World w : worlds) {
                if(w.getName().compareToIgnoreCase(_world)==0) actualWorld = w;
                
            }
            int X=0;
            int Z=0;
            if(actualWorld!=null){
                // grab the positions
                Set<String> Xs = fc.getConfigurationSection("flchunk."+_world).getKeys(false);
                for (String _x : Xs) {
                    X=Integer.parseInt(_x);
                    Set<String> Zs = fc.getConfigurationSection("flchunk."+_world+"."+_x).getKeys(false);
                    for (String _z : Zs) {
                        Z=Integer.parseInt(_z);

                        Location chnkLocation = new Location(actualWorld, X, 0, Z);
                        Chunk loadThisChunk = chnkLocation.getChunk();
                        String Out = _world+": X:"+_x+"; Z:"+_z;
                        if(!loadThisChunk.isForceLoaded()){
                            Out+= "\n* Force load not set";
                        }

                        if(!loadThisChunk.isLoaded()){
                            Out+= "\n* Chunk is not loaded!";
                        }

                        sender.sendMessage(Out);
                    }
                }
            }else{
                sender.sendMessage(ChatColor.RED+"ERROR: World '"+_world+"' is not loaded, and chunks cannot be force-loaded in it");
            }
        }


        return true;
    }
    
}