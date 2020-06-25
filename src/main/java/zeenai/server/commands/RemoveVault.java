package zeenai.server.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

import zeenai.server.Main;

public class RemoveVault implements CommandExecutor {

    @Override
    public boolean onCommand( CommandSender sender,  Command command,  String label,
             String[] args)
    {
        if(args.length!=1)return false;

        Player p = (Player)sender;
        // Delete this player's specified vault and then rez out the items in game at the player's location to avoid item loss
        // NukeVault should be used if you wish to delete from game entirely
        FileConfiguration fc = PlayerVault.GetConfig(p);
        if(!fc.contains(p.getName()+".vault."+args[0])){
            sender.sendMessage("That vault does not exist");
            return true;
        }
        List<ItemStack> lIS = (List<ItemStack>)fc.get(p.getName()+".vault."+args[0]);

        fc.set(p.getName()+".vault."+args[0],null);
        PlayerVault.SaveConfig(p);

        for(ItemStack is : lIS){
            if(is!=null)
                p.getWorld().dropItem(p.getLocation().add(0, 3, 0), is);
        }

        return true;
    }
    
}