package zeenai.server.colormagic;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import zeenai.server.Main;

public class PrefixColor implements CommandExecutor {

    private final Inventory prefixMenu;

    public PrefixColor(Inventory i){
        prefixMenu = i;
    }

    @Override
    public boolean onCommand( CommandSender sender,  Command command,  String label,
             String[] args)
    {
        if(!sender.hasPermission("znimod.customPrefix")){
            sender.sendMessage(ChatColor.RED+"You cannot use custom prefixes. Changing prefix color is disabled");
            return true;
        }
        Player p = (Player)sender;

        Inventory inv = Bukkit.createInventory(null, prefixMenu.getSize(), "Prefix Color Menu");
        inv.setContents(prefixMenu.getContents());
        p.openInventory(inv);

        return true;
    }
    
}