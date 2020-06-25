package zeenai.server.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import org.bukkit.*;
import org.bukkit.entity.*;

import zeenai.server.Main;

public class NukeVault implements CommandExecutor {

    @Override
    public boolean onCommand( CommandSender sender,  Command command,  String label,
             String[] args)
    {
        if(args.length!=2)return false;

        Player p = (Player)sender;

        if(!p.hasPermission("znimod.adminCommands")){
            sender.sendMessage("You lack required permissions");
            return false;
        }

        // Delete this player's specified vault and then rez out the items in game at the player's location to avoid item loss
        // NukeVault should be used if you wish to delete from game entirely
        FileConfiguration fc = PlayerVault.GetConfig(p);
        if(!fc.contains(args[0]+".vault."+args[1])){
            sender.sendMessage("That vault does not exist");
            return true;
        }

        fc.set(args[0]+".vault."+args[1],null);
        PlayerVault.SaveConfig(p);

        if(args[0].compareToIgnoreCase(p.getName())==0){
            Bukkit.broadcastMessage(p.getName()+" has dropped a nuke on one of their own vaults!");
        } else {
            Bukkit.broadcastMessage(p.getName()+" has dropped a nuke on one of "+args[0]+"'s vaults ["+args[1]+"]");
        }

        return true;
    }
    
}