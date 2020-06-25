package zeenai.server.biomewatchdog;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.md_5.bungee.api.ChatColor;
import zeenai.server.Main;
import zeenai.server.PlayerConfig;

public class CheckForNewBiome implements Listener
{
    @EventHandler
    public void CheckForANewBiome(PlayerMoveEvent ev)
    {
        if(!PlayerConfig.GetConfig(ev.getPlayer()).getBoolean("ignorebiomes")){
            // OK
                
            if(ev.getPlayer().getLocation().getBlock().getBiome() != Main.GetMainInstance().PlayerLastBiome(ev.getPlayer().getName())){
                Main.GetMainInstance().SetPlayerLastBiome(ev.getPlayer().getName(), ev.getPlayer().getLocation().getBlock().getBiome());
                ev.getPlayer().sendMessage("[Server] "+ChatColor.GREEN+" You have entered biome : "+ChatColor.DARK_PURPLE+ev.getPlayer().getLocation().getBlock().getBiome().name());

            }
        }else{
            // ignore the event
        }
    }


    @EventHandler
    public void FlushOnLogin(PlayerLoginEvent ev){
        if(Main.GetMainInstance().BiomesMap.containsKey(ev.getPlayer().getName()))Main.GetMainInstance().BiomesMap.remove(ev.getPlayer().getName());
    }

    @EventHandler
    public void FlushOnQuit(PlayerQuitEvent ev){
        if(Main.GetMainInstance().BiomesMap.containsKey(ev.getPlayer().getName()))Main.GetMainInstance().BiomesMap.remove(ev.getPlayer().getName());
    }
}