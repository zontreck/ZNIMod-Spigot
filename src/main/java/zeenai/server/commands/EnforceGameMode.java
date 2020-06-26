package zeenai.server.commands;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import zeenai.server.Main;

public class EnforceGameMode implements Listener{
    @EventHandler
    public void ChangedWorld(PlayerChangedWorldEvent event){
        Player play = event.getPlayer();
        
        if(!play.hasPermission("znimod.keepGamemode"))return;

        Main.GetMainInstance().getServer().getScheduler().scheduleSyncDelayedTask(Main.GetMainInstance(), new Runnable(){
            @Override
            public void run(){

                String LastGameMode = Main.GetMainInstance().getConfig().getString("LastGameMode");
                if(LastGameMode == play.getGameMode().toString()){
                    // do nothing
                    return;
                }else{
                    play.setGameMode(GameMode.valueOf(LastGameMode));
                }
            }
        }, 500L);
    }
}