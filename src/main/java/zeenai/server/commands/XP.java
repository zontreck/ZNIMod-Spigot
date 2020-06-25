package zeenai.server.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import zeenai.server.Main;

public class XP implements CommandExecutor {

    @Override
    public boolean onCommand( CommandSender sender,  Command command, String label,
             String[] args)
    {
        if(args.length!=3){
            return false;
        }
        if(Bukkit.getOnlinePlayers()==null)return false;
        List<Player> Players = new ArrayList(Bukkit.getOnlinePlayers());
        if(!sender.hasPermission("znimod.adminCommands")){
            sender.sendMessage("You lack permission");
            return false;
        }
        for(Player p : Players){
            Main.GetMainInstance().getLogger().info(p.getName()+" -  xp search");
        }
        // this is a op only command!
        switch(args[0]){
            case "add":
            {
                String PlayerName = args[1];
                int Amount = Integer.parseInt(args[2]);
                boolean found=false;
                for(Player p : Players){
                    if(p.getName().compareToIgnoreCase(PlayerName) == 0){
                        p.giveExp(Amount);
                        found=true;
                    }
                }
                if(!found){
                    sender.sendMessage("Failed to find the player '"+PlayerName+"'");
                    return false;
                }

                break;
            }
            case "rem":
            {
                String PlayerName = args[1];
                boolean found=false;
                for(Player p : Players){
                    if(p.getName().compareToIgnoreCase(PlayerName) == 0){
                        int curPoints = p.getTotalExperience();
                        curPoints-=Integer.parseInt(args[2]);
                        found=true;
                        p.setTotalExperience(0);
                        p.giveExp(curPoints);
                    }
                }

                if(!found){
                    sender.sendMessage("Failed to find the player '"+PlayerName+"'");
                    return false;
                }
                break;
            }
            case "get":
            {
                String PlayerName = args[1];
                boolean found = false;
                for(Player p : Players){
                    if(p.getName().compareToIgnoreCase(PlayerName)==0){
                        sender.sendMessage("XP Points for '"+PlayerName+"' = "+p.getTotalExperience());
                        found=true;
                    }
                }
                if(!found){
                    sender.sendMessage("Could not find player '"+PlayerName+"'");
                    return false;
                }

                break;
            }
        }
        sender.sendMessage("/XP Finished!");
        return true;
    }
    
}