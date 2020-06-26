package zeenai.server.commands;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import zeenai.server.Main;
import zeenai.server.PlayerConfig;

public class EnforceGameMode implements Listener{
    @EventHandler
    public void ChangedWorld(PlayerChangedWorldEvent event){
        Player play = event.getPlayer();
        
        if(!play.hasPermission("znimod.keepGamemode"))return;

        DoGameModeSchedule(play);


        if(!play.hasPermission("znimod.keepFly"))return;
        DoFlySchedule(play);

    }

    private void DoFlySchedule(Player play){
        Main.GetMainInstance().getServer().getScheduler().scheduleSyncDelayedTask(Main.GetMainInstance(), new Runnable(){
        
            @Override
            public void run() {
                boolean LastFly = PlayerConfig.GetConfig(play).getBoolean("flight");
                if(LastFly == play.getAllowFlight())return;
                else{
                    play.setAllowFlight(LastFly);
                }
            }
        }, 50L);
    }

    private void DoGameModeSchedule(Player play) {
        Main.GetMainInstance().getServer().getScheduler().scheduleSyncDelayedTask(Main.GetMainInstance(), new Runnable(){
            @Override
            public void run(){

                String LastGameMode = PlayerConfig.GetConfig(play).getString("LastGameMode");
                if(LastGameMode == play.getGameMode().toString()){
                    // do nothing
                    return;
                }else{
                    play.setGameMode(GameMode.valueOf(LastGameMode));
                }
            }
        }, 50L);
    }

    @EventHandler
    public void LoginEvent(PlayerJoinEvent ev){
        Player play = ev.getPlayer();
        if(!play.hasPermission("znimod.keepGamemode"))return;
        DoGameModeSchedule(play);
        if(!play.hasPermission("znimod.keepFly"))return;
        DoFlySchedule(play);
    }

    @EventHandler
    public void GameModeChanged(PlayerGameModeChangeEvent ev){
        Player play = ev.getPlayer();
        if(!play.hasPermission("znimod.keepGamemode"))return;
        DoGameModeSchedule(play);

        if(!play.hasPermission("znimod.keepFly"))return;
        DoFlySchedule(play);
    }
}