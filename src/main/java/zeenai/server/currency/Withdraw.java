package zeenai.server.currency;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import zeenai.server.Main;

public class Withdraw implements CommandExecutor {

    @Override
    public boolean onCommand( CommandSender sender,  Command command,  String label,
             String[] args)
    {
        // Get the currency config
        FileConfiguration fc = PlayerCurrencyBoard.GetConfig((Player)sender);
        int Emeralds = Currency.GetReward(Material.EMERALD);

        double BAL = Main.GetMainInstance().econ.getBalance((Player)sender);

        int total = Integer.parseInt(args[0]); // number of emeralds to withdraw
        Emeralds = Emeralds*total;
        if(BAL>=Emeralds) {
            // Do withdraw
            
            Main.GetMainInstance().econ.withdrawPlayer((Player)sender, Emeralds);
        }
        ItemStack is = new ItemStack(Material.EMERALD, total);
        
        Player p = (Player)sender;
        p.getInventory().addItem(is);

        return true;
    }
    
}