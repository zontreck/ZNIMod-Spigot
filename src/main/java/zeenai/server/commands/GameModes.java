package zeenai.server.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import zeenai.server.Main;


public class GameModes implements CommandExecutor, Listener {
    

    @Override
    public boolean onCommand( CommandSender sender,  Command command,  String label,
             String[] args)
    {
        Player p = (Player)sender; // always default to executor
        if(!sender.hasPermission("znimod.adminCommands")){
            sender.sendMessage("You lack permissions");
            return false;
        }
        if(args.length==2){
            // grab the target player
            boolean found=false;
            List<Player> Players = new ArrayList<>(Bukkit.getOnlinePlayers());
            for(Player x : Players){
                if(x.getName().compareToIgnoreCase(args[1])==0){
                    p=x;
                    found=true;
                }
            }

            if(!found){
                sender.sendMessage("Failed to find the target player!");
                return false;
            }
        }

        GameMode finalGm = GameMode.SURVIVAL;
        int mode = Integer.parseInt(args[0]);
        switch(mode){
            case 0:{
                finalGm = GameMode.SURVIVAL;
                break;
            }
            case 1:{
                finalGm = GameMode.CREATIVE;
                break;
            }
            case 2:{
                finalGm = GameMode.SPECTATOR;
                break;
            }
            case 3:{
                finalGm = GameMode.ADVENTURE;
                break;
            }
        }

        FileConfiguration conf = Main.GetMainInstance().getConfig();
        conf.set("LastGameMode", finalGm.toString());
        Main.GetMainInstance().saveConfig();
        
        p.setGameMode(finalGm);

        return true;
    }

    
}