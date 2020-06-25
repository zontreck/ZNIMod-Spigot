package zeenai.server.chunkforceloader;

import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldLoadEvent;

import net.md_5.bungee.api.ChatColor;
import zeenai.server.Main;

public class FLChunk implements CommandExecutor, Listener {

    @Override
    public boolean onCommand( CommandSender sender,  Command command,  String label,
             String[] args)
    {
        // Get the chunk coords and add to config and keep this chunk loaded

        Player p = (Player)sender;
        if(!p.hasPermission("znimod.opCommands")){
            sender.sendMessage("You lack permissions");
            return false;
        }
        Location L = p.getLocation();
        Chunk c = L.getChunk();


        FileConfiguration fc = Main.GetMainInstance().getConfig();
        // toggle the status
        if(fc.contains("flchunk."+c.getWorld().getName()+"."+c.getX()+"."+c.getZ())){
            fc.set("flchunk."+c.getWorld().getName()+"."+c.getX()+"."+c.getZ(),null);
            sender.sendMessage("Disabled force loading in this chunk");
            c.setForceLoaded(false);
        }else{

            sender.sendMessage("Enabled force loading in this chunk");
            fc.set("flchunk."+c.getWorld().getName()+"."+c.getX()+"."+c.getZ(), true);
            c.setForceLoaded(true);
        }
        Main.GetMainInstance().saveConfig();
        



        return true;
    }

    public void ScanAndLoad(boolean unload){
        // On player login scan our config for chunks in the world they login to

        // start scan: config
        FileConfiguration fc = Main.GetMainInstance().getConfig();
        if(!fc.contains("flchunk"))return;
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
                        if(!loadThisChunk.isForceLoaded()){
                            if(!unload)
                                loadThisChunk.setForceLoaded(true);
                        }

                        if(!loadThisChunk.isLoaded()){
                            if(!unload)
                                loadThisChunk.load();
                        }
                        if(unload){
                            loadThisChunk.setForceLoaded(false);
                            loadThisChunk.unload(true);
                        }
                        
                    }
                }
            }else{
                Bukkit.broadcastMessage(ChatColor.RED+"ERROR: World '"+_world+"' is not loaded, and chunks cannot be force-loaded in it");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent e){
        ScanAndLoad(false);
    }


    @EventHandler (priority = EventPriority.HIGHEST)
    public void onWorldLoad(WorldLoadEvent e){
        if(Bukkit.getOnlinePlayers().size()!=0)
            ScanAndLoad(false);
        else
            ScanAndLoad(true);
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void Deload(PlayerQuitEvent e){
        if(Bukkit.getOnlinePlayers().size()==0){
            // un-forceload!
            ScanAndLoad(true);
        }
    }


    
    
}