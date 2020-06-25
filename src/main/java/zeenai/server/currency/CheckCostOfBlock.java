package zeenai.server.currency;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CheckCostOfBlock implements CommandExecutor {

    @Override
    public boolean onCommand( CommandSender sender,  Command command,  String label,
             String[] args)
    {
        Player p = (Player)sender;

        ItemStack held = p.getInventory().getItemInMainHand();
        p.sendMessage("Block : "+held.getType().toString()+" = "+Currency.GetReward(held.getType()));
        return true;
    }
    
}