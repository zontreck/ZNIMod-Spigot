package zeenai.server.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class PlayerVaultStackPush implements CommandExecutor {

    @Override
    public boolean onCommand( CommandSender sender,  Command command,  String label,
             String[] args)
    {
        // Push inventory for player onto stack
        Player p = (Player)sender;
        int i =0;
        List<ItemStack> push = new ArrayList<ItemStack>();
        if(PlayerVault.GetConfig(p).contains(p.getName()+".vault."+args[0])) push = (List<ItemStack>)PlayerVault.GetConfig(p).get(p.getName()+".vault."+args[0]);
        else{
            // check permissions
            if(!sender.hasPermission("znimod.unlimitedVC") && PlayerVault.GetConfig(p).getConfigurationSection(p.getName()+".vault").getKeys(false).size() >= 5){
                // Throw error
                sender.sendMessage(ChatColor.RED+"Too many virtual chests already in use. You need VIP perks to have more than 5");
                return true;
            }
        }
        int end = p.getInventory().getSize();
        for(i=0;i<end;i++){
            if(p.getInventory().getItem(i)!= null){
                try{

                    push.add(p.getInventory().getItem(i));
                    p.getInventory().setItem(i, null);
                } catch(Exception e){
                    sender.sendMessage("Vault is now full");
                    break;
                }
            }
        }

        PlayerVault.GetConfig(p).set(p.getName()+".vault."+args[0], push);
        PlayerVault.SaveConfig(p);

        

        return true;
    }
    
}