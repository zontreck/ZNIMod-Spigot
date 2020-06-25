package zeenai.server.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import zeenai.server.Main;

public class ZNIReload implements CommandExecutor, Listener {

    @Override
    public boolean onCommand( CommandSender sender,  Command command,  String label,
             String[] args)
    {

        if(!sender.hasPermission("znimod.adminCommands")){
            sender.sendMessage("You lack permissions");
            return false;
        }

        sender.sendMessage("Reloading ZNIMod configuration.. stand by");
        ZNIReload.doReload(); // This will force reload next time the configs are accessed
        sender.sendMessage("Reload completed");

        return true;
    }

    public static void doReload(){
        Main.GetMainInstance().reloadConfig();
        Main.GetMainInstance().CustomConfigs.clear();
        Main.GetMainInstance().stateRequests.clear();
        Main.GetMainInstance().states.clear();
        Main.GetMainInstance().forceQueue.clear();
        Main.GetMainInstance().boards.clear();
        Main.GetMainInstance().displayNames.clear();
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent ev)
    {
        ZNIReload.doReload();
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent ev){
        ev.getPlayer().sendMessage("Flushing ZNIMod Configuration");
        ZNIReload.doReload(); // Flush invalid data
    }

}