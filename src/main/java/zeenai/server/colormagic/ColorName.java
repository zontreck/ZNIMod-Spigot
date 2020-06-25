package zeenai.server.colormagic;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import zeenai.server.Main;

public class ColorName implements CommandExecutor {

    private final Inventory prefixMenu;

    public ColorName(Inventory i){
        prefixMenu = i;
    }

    @Override
    public boolean onCommand( CommandSender sender,  Command command,  String label,
             String[] args)
    {
        Player p = (Player)sender;

        Inventory inv = Bukkit.createInventory(null, prefixMenu.getSize(), "Name Color Menu");
        inv.setContents(prefixMenu.getContents());
        p.openInventory(inv);

        return true;
    }
    
}