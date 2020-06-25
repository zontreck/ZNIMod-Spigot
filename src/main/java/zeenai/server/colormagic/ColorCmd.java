package zeenai.server.colormagic;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import zeenai.server.Main;

public class ColorCmd implements CommandExecutor {

    private final Inventory colorMenu;

    public ColorCmd(Inventory i){
        colorMenu = i;
    }

    @Override
    public boolean onCommand( CommandSender sender,  Command command,  String label,
             String[] args)
    {
        Player p = (Player)sender;

        Inventory inv = Bukkit.createInventory(null, colorMenu.getSize(), "Chat Color Menu");
        inv.setContents(colorMenu.getContents());
        p.openInventory(inv);

        return true;
    }
    
}