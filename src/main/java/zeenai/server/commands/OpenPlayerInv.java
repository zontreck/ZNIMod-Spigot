package zeenai.server.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class OpenPlayerInv implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length != 1)return false;


        Player p = (Player)sender;
        boolean found=false;
        List<Player> Players = new ArrayList<>(Bukkit.getOnlinePlayers());
        for (Player player : Players) {
            if(player.getName().compareToIgnoreCase(args[0])==0){
                // open this inventory!
                if(p.hasPermission("znimod.adminCommands"))
                    p.openInventory(player.getInventory());
                else{
                    Inventory tmp = Bukkit.createInventory(null, 54, args[0]+"'s inventory (Read Only)");
                    tmp.setContents(player.getInventory().getContents());
                    p.openInventory(tmp);
                }
                found=true;
                break;
            }
        }
        if(!found){
            sender.sendMessage("Could not find that player online!");
            return false;
        }
        return true;
    }
    
}