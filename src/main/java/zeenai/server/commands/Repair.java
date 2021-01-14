package zeenai.server.commands;

import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import zeenai.server.Main;
import zeenai.server.currency.Currency;
import zeenai.server.currency.PlayerCurrencyBoard;

public class Repair implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        // This method buys a full repair for whatever item you have in your hand.
        // The cost can be explained as so
        /*
        1: Base price: 200 Emeralds
        2: 75 Emeralds per enchantment
        3: 1 emerald for wood
        4: 5 emeralds for stone
        5: 10 emeralds for iron
        6: 20 emeralds for gold
        7: 30 emeralds for diamond
        8: 40 emeralds for netherite - coming soon with 1.16
        */
        double price = 0.0;
        double costOfEmeralds = Currency.GetReward(Material.EMERALD);
        price = 200*costOfEmeralds;
        Player p = (Player)sender;
        double balance = PlayerCurrencyBoard.GetConfig(p).getDouble("balance");
        ItemStack i = p.getInventory().getItemInMainHand();
        ItemMeta metaValues = i.getItemMeta();
        Map<Enchantment,Integer> enchants = metaValues.getEnchants();
        double enchantRepairAdd = (75.0*costOfEmeralds)*enchants.size();
        price+=enchantRepairAdd;

        String type = i.getType().toString();
        if(type.contains("wood")){
            price+=costOfEmeralds;
        }else if(type.contains("stone")){
            price+=5*costOfEmeralds;
        } else if(type.contains("iron")){
            price+=10*costOfEmeralds;
        } else if(type.contains("gold")){
            price+=20*costOfEmeralds;
        } else if(type.contains("diamond")){
            price+=30*costOfEmeralds;
        } else if(type.contains("netherite")){
            price+=40*costOfEmeralds;
        }

        if(balance>=price){
            //Main.GetMainInstance().econ.withdrawPlayer(p, price);
            balance -=price;
            PlayerCurrencyBoard.GetConfig(p).set("balance", balance);
            PlayerCurrencyBoard.SaveConfig(p);

            p.sendMessage("["+ChatColor.RED+"Repair"+ChatColor.WHITE+"] You pay "+price+" to repair '"+i.getType().name()+"'");
        } else {
            p.sendMessage("You lack sufficient funds. Needed for repair: "+price);
            return false;
        }
        p.sendMessage("Repairing...");
        if(!i.getType().isItem()){
            p.sendMessage("This can only be used on items");
            ///Main.GetMainInstance().econ.depositPlayer(p, price);
            balance += price;
            PlayerCurrencyBoard.GetConfig(p).set("balance", balance);
            PlayerCurrencyBoard.SaveConfig(p);
            return true;
        }
        try{

            ItemStack si = new ItemStack(i.getType(), i.getAmount());
            for (Enchantment en : i.getEnchantments().keySet()) {
                try{
                    si.addEnchantment(en, i.getEnchantments().get(en));
                }catch(Exception ex){
                    p.sendMessage(ChatColor.RED+"Could not add enchantment '"+en.toString()+"' with a level of "+i.getEnchantments().get(en));
                    double refund = 75.0*costOfEmeralds;
                    p.sendMessage("You have been refunded: "+refund);
                    balance += refund;
                    PlayerCurrencyBoard.GetConfig(p).set("balance", balance);
                    PlayerCurrencyBoard.SaveConfig(p);
                    //Main.GetMainInstance().econ.depositPlayer(p, refund);
                }
            }
            
            p.getInventory().setItemInMainHand(si);
            p.sendMessage("Repair completed!");
        }catch(Exception e){
            balance += price;
            PlayerCurrencyBoard.GetConfig(p).set("balance", balance);
            PlayerCurrencyBoard.SaveConfig(p);
            //Main.GetMainInstance().econ.depositPlayer(p, price);
            p.sendMessage("An error has occurred. You were not charged for repair");
            p.sendMessage(ChatColor.RED+e.getMessage());
        }

        return true;
    }
    
}