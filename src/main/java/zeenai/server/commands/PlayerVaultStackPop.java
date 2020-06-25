package zeenai.server.commands;


import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import zeenai.server.Main;

public class PlayerVaultStackPop implements CommandExecutor {

    @Override
    public boolean onCommand( CommandSender sender,  Command command,  String label,
             String[] args)
    {
        // Push inventory for player onto stack
        Player p = (Player)sender;
        
        int slot=0;
        List<ItemStack> pop = (List<ItemStack>)Main.GetMainInstance().getConfig().get(p.getName()+".vault."+args[0]);
        for(ItemStack is : pop){
            // Put item back into player inventory
            if(is != null){
                p.getInventory().setItem(slot, is);
            }
            slot++;
        }

        p.sendMessage("Completed inventory pop");
        PlayerVault.GetConfig(p).set(p.getName()+".vault."+args[0],null);
        PlayerVault.SaveConfig(p);
        

        return true;
    }
    
}