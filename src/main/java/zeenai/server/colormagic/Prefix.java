package zeenai.server.colormagic;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import zeenai.server.Main;

public class Prefix implements CommandExecutor {


    @Override
    public boolean onCommand( CommandSender sender,  Command command,  String label,
             String[] args)
    {
        Player p = (Player)sender;
        String ret="";
        // store prefix text!
        FileConfiguration fc = Main.GetMainInstance().getConfig();
        for(String X : args){
            ret +=X+" ";
        }

        if(ret.endsWith(" "))ret = ret.substring(0, ret.length()-1);

        // if user has the custom prefix permission, then what they entered will be used instead
        if(sender.hasPermission("znimod.customPrefix")){
            fc.set(p.getName()+".prefix", ret);
            p.sendMessage(ChatColor.GREEN+"Your prefix has been set");
        }else{

            if(Main.GetMainInstance().displayNames.get(p.getName()).internal.containsKey(ret.toLowerCase())){
                fc.set(p.getName()+".prefix", ret);
                p.sendMessage(ChatColor.GREEN+"Your prefix has been set");
                fc.set(p.getName()+".prefixColor", Main.GetMainInstance().displayNames.get(p.getName()).internal.get(ret.toLowerCase()).getChar());
            }
            else{
                p.sendMessage("You do not have that prefix available. To see available options, use the /prefixes command");
            }
        }

        Main.GetMainInstance().saveConfig();

        return true;
    }
    
}