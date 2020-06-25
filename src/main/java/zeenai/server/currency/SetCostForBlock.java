package zeenai.server.currency;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;

public class SetCostForBlock implements CommandExecutor {

    @Override
    public boolean onCommand( CommandSender sender,  Command command,  String label,
             String[] args)
    {
        // Sets the cost for this block
        Player p = (Player)sender;
        ItemStack i = p.getInventory().getItemInMainHand();

        if(p.hasPermission("znimod.adminCommands")){
            Currency.SetReward(i.getType(), Integer.parseInt(args[0]));

            p.sendMessage("Reward set");
            return true;
        }else{
            p.sendMessage(ChatColor.RED+"Reward could not be set. You lack permission");
            return false;
        }
    }
    
}